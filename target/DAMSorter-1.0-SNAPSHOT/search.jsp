
<%-- 
    Document   : search
    Created on : May 27, 2013, 3:47:23 PM
    Author     : acdr4
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>

<html>
    <head>
        <meta charset="utf-8" />
        <title>MM7 ObjectID & BibID Sort</title>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
        <script src="http://code.jquery.com/jquery-1.8.2.js"></script>
        <script src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
        <link rel="stylesheet" type="text/css" href="search.css"/>

        <!--create a useBean session that will fetch JSON for the searched object -->
        <jsp:useBean id="searchbean" scope="session" class="edu.yale.damsorter.SearchQueryHandler" />
        
        <%
            String s_by = request.getParameter("search_by");
            String s_id = request.getParameter("search_id");
            String p_only = request.getParameter("pub_only");
        %>
        <jsp:setProperty name="searchbean" property="search_by" value="<%=s_by%>"/>
        <jsp:setProperty name="searchbean" property="search_id" value="<%=s_id%>"/>
        <jsp:setProperty name="searchbean" property="pub_only" value="<%=p_only%>"/>
        <%       
            searchbean.path = pageContext.getServletContext().getRealPath("/");
            searchbean.url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            String json = searchbean.getJSON();
        %>

        <script>

            //var old_position;
            var new_position = new Array();
            var recordsObj;
    
            window.onbeforeunload = function() {
                return "Did you save your work?";
            };
    
            //called when objects are sorted/re-arranged
            $(function() {
                $('#sortable').sortable({
                    update: function(event, ui) {
                        // grabs the new positions now that we've finished sorting
                        //clean method removes unwanted empty strings
                        new_position = $(this).sortable('toArray').clean("");
                        updateRank(new_position);
                    }
                });
                $( "#sortable" ).disableSelection();
            });
            
            // for a busy icon during ajax requests
            /* $.ajaxSetup({
                beforeSend:function(){
                    // show image here
                    $("#busy_icon").show();
                },
                complete:function(){
                    // hide image here
                    $("#busy_icon").hide();
                }
            });*/
            
            //extends native Array prototype to enable removal of all instances of param deleteValue from array object
            Array.prototype.clean = function(deleteValue)
            {
                for (var i = 0; i < this.length; i++) {
                    if (this[i] == deleteValue) {         
                        this.splice(i, 1);
                        i--;
                    }
                }
                return this;
            }
            
            //helper function that alerts data
            function showData(data)
            {
                alert(data);
            }
            
            //sets primaryIdx to object with index provided
            function setPrimary(index)
            {
                for(i = 0; i < recordsObj.recordsArr.length; i++)
                {
                    if (i == index)
                        recordsObj.recordsArr[i].primary = "Y";
                    else
                        recordsObj.recordsArr[i].primary = "N";
                }
                //recordsObj.primaryIdx = index;
                //displayRecordsObj("primaryIdx");
            }
            
            
            //sets cds level of the object with index provided
            /*
            function setCDSLevel(index)
            {
                recordsObj.recordsArr[index].cdsLevel = $('#cds_level'+index+'').val();
                //displayRecordsObj("cdsLevel");
            }
            */
           
            function setKeywords(value, index)
            {
                recordsObj.recordsArr[index].keywords = value;
            }
            
            function setDescCaption(value, index)
            {
                recordsObj.recordsArr[index].descCaption = value;
            }
           
            //update all the ranks as per the newly sorted objects
            function updateRank(posArr)
            {
                var rankArr = new Array(posArr.length);
                for(i = 0; i < posArr.length; i++)
                {
                    rankArr[posArr[i]] = i+1;
                }
                $.each(recordsObj.recordsArr, function(i) {
                    recordsObj.recordsArr[i].rank = rankArr[i];
                });
            }
            
            //helper function to display various contents of 
            //IMG I alert(JSON.stringify(recordsObj));  JSON object
            function displayRecordsObj(param)
            {
                var temp = [];
                if(param == "id") {
                    for(i = 0; i < recordsObj.recordsArr.length; i++){
                        temp.push(recordsObj.recordsArr[i].id);
                    }
                }
                else if(param == "rank"){
                    for(i = 0; i < recordsObj.recordsArr.length; i++){
                        temp.push(recordsObj.recordsArr[i].rank);
                    }
                }
                else if(param == "primary"){
                    for(i = 0; i < recordsObj.recordsArr.length; i++){
                        temp.push(recordsObj.recordsArr[i].primary);
                    }
                }
                /*
                else if(param == "primaryIdx"){
                    temp.push(recordsObj.primaryIdx);
                }
                else if(param == "cdsLevel"){
                    for(i = 0; i < recordsObj.recordsArr.length; i++){
                        temp.push(recordsObj.recordsArr[i].cdsLevel);
                    }
                }
                */
                //alert(temp);
            }
            
            // executed when search button is clicked...
            // posts form data (search_by and search_id) to search.jsp
            /*function postSearch()
            {
                alert($('#getData').serialize());
                $("#busy_icon").show();
                $.ajax({
                    url: "search.jsp",
                    method: "POST",
                    data: $('#getData').serialize(),
                    success: function(){
                        $("#busy_icon").hide();
                    },
                    error: function() {
                        $("#busy_icon").hide();
                        alert("ERROR: Failed to search DAM.");
                    }
                });
            }*/
            
            //executed when save button is clicked...posts recordsObj to save.jsp
            function postJson()
            {
                //alert(JSON.stringify(recordsObj));
                // confirm window pops up for safety before data is written in DAM
                var check = confirm("Changes will be written to DAM. Press OK to proceed.");
                if (check == true)
                {
                    $("#busy_icon").show();
                    $.ajax({
                        url:    "save.jsp",
                        type:   "post",
                        data:   "json_data="+JSON.stringify(recordsObj),
                        success: function(){
                            $("#busy_icon").hide();
                            alert("Changes successfully posted to DAM!");
                        },
                        error: function() {
                            $("#busy_icon").hide();
                            alert("ERROR: Failed to write to DAM.");
                        }
                    });
                }                
            }
            
            // makes an options tag element with options including numbers form start to end (inclusive)
            /*
            function makeCdsOptionsTag(start, end)
            {
                var tagString = "";
                for(i = start; i <= end; i++)
                {
                    tagString += '<option value="'+ i +'">'+ i +'</option>';
                }
                return tagString;
            }
            */
            function sortResults(objData, prop, asc) {
                objData = objData.sort(function(a, b) {
                    if (asc) return (a[prop] > b[prop]) ? 1 : ((a[prop] < b[prop]) ? -1 : 0);
                    else return (b[prop] > a[prop]) ? 1 : ((b[prop] < a[prop]) ? -1 : 0);
                });
                //alert(JSON.stringify(objData));
                return objData;
            }
            
            function sortData(sort_by) {
                if (recordsObj.recordsArr.length > 1) {
                    // sort accending for all except primary
                    if(sort_by != "primary")
                        recordsObj.recordsArr = sortResults(recordsObj.recordsArr, sort_by, true);
                    else
                        recordsObj.recordsArr = sortResults(recordsObj.recordsArr, sort_by, false);
                 
                    $('#caption').empty();
                    //$('#caption').append('<b>Displaying search results for '+ search_by +
                    //    ' '+ recordsObj.search_id +'</b>');
                    $('#sortable').empty();
                    $('#sortable').append('<hr/>');
                    
                    //reset position array
                    new_position = [];
                    
                    //set up each image div from json
                    $.each(recordsObj.recordsArr, function(i) {
                        $('#sortable').append('<li class="ui-state-default" id="'+i+'">'+
                            //'<div onClick="showData(\''+recordsObj.recordsArr[i].id+'\');">'+
                            '<div>'+
                              '<img alt="'+recordsObj.recordsArr[i].id+'" src="'+recordsObj.recordsArr[i].thumb+'">'+
                            '</div>'+
                            '<div>'+recordsObj.recordsArr[i].filename+'</div>'+
                            '<div>'+
                              'Current Rank: '+recordsObj.recordsArr[i].rank+
                            '</div>'+
                            '<div>'+
                                'Primary: <input id="is_primary'+i+'" type="radio" name="primary" value="primary"'+' onClick="setPrimary('+i+')"/>'+
                            '</div>'+
                            '<div>'+
                              'Keywords (for LOD): <input id="keywords'+i+'" type="text" name="" value="'+recordsObj.recordsArr[i].keywords+'" onchange="setKeywords(keywords'+i+'.value, '+i+')"/>'+
                            '</div>'+
                            '<div>'+
                              'Caption (for Web): <input id="descCaption'+i+'" type="text" name="" value="'+recordsObj.recordsArr[i].descCaption+'" onchange="setDescCaption(descCaption'+i+'.value, '+i+')"/>'+
                            '</div>'+
                            '</li>');
                        if(recordsObj.recordsArr[i].primary == "Y")
                            $("#is_primary"+i).attr('checked', 'checked');
                            
                        new_position.push(i);
                    });
 
                    //if(recordsObj.primaryIdx >= 0)
                    //    $("#is_primary"+recordsObj.primaryIdx).attr('checked', 'checked');

                    new_position = $('#sortable').sortable('toArray').clean("");
                    updateRank(new_position);
                    //alert(new_position);
                }
            }
            
            //extract JSON data and display
            function parseJson(json){
                recordsObj = json;
                new_position = [];
                
                //alert(JSON.stringify(recordsObj));
                
                $('#searchID').val(recordsObj.search_id);
                var search_by = "";
                if(recordsObj.search_by == "bibid") {
                    search_by = "Orbis Bib ID";
                    $('#searchBy').val('bibid');
                } else {
                    search_by = "TMS Object ID";
                    $('#searchBy').val('objectid');
                }
                $('#pub_onlyID').val(recordsObj.pub_only);

                // specify range of CDS level here!
                //var cdsOptionsTag = makeCdsOptionsTag(0,18);
                //parse JSON only if it's not empty
                if (recordsObj.recordsArr.length > 0)
                {   
                    $('#caption').empty();
                    //$('#caption').append('<b>Displaying search results for '+ search_by +
                    //    ' '+ recordsObj.search_id +'</b>');
                    $('#sortable').empty();
                    $('#sortable').append('<hr/>');
                    //set up each image div from json
                    $.each(recordsObj.recordsArr, function(i) {
                        $('#sortable').append('<li class="ui-state-default" id="'+i+'">'+
                            //'<div onClick="showData(\''+recordsObj.recordsArr[i].id+'\');">'+
                            '<div>'+
                              '<img alt="'+recordsObj.recordsArr[i].id+'" src="'+recordsObj.recordsArr[i].thumb+'">'+
                            '</div>'+
                            '<div>'+recordsObj.recordsArr[i].filename+'</div>'+
                            '<div>'+
                              'Current Rank: '+recordsObj.recordsArr[i].rank+
                            '</div>'+
                            '<div>'+
                              'Primary: <input id="is_primary'+i+'" type="radio" name="primary" value="primary"'+'onClick="setPrimary('+i+')"/>'+
                            '</div>'+
                            '<div>'+
                              'Keywords (for LOD): <input id="keywords'+i+'" type="text" name="" value="'+recordsObj.recordsArr[i].keywords+'" onchange="setKeywords(keywords'+i+'.value, '+i+')"/>'+
                            '</div>'+
                            '<div>'+
                              'Caption (for Web): <input id="descCaption'+i+'" type="text" name="" value="'+recordsObj.recordsArr[i].descCaption+'" onchange="setDescCaption(descCaption'+i+'.value, '+i+')"/>'+
                            '</div>'+
                            '</li>');
                        if(recordsObj.recordsArr[i].primary == "Y")
                            $("#is_primary"+i).attr('checked', 'checked');
                        /*
                         'CDS Level:<select id="cds_level'+i+
                            '" onchange = setCDSLevel('+i+')>'+
                            cdsOptionsTag +
                            '</select>
                         */
                        //set the appropriate default cds level
                        //$('#cds_level'+i).val(recordsObj.recordsArr[i].cdsLevel);
                        //initialize positions array with default ranks i.e 0,1,2,3...
                        new_position.push(i);
                    });
                    //check the appropriate primary radio button
                    //if(recordsObj.primaryIdx >= 0)
                        //$("#is_primary"+recordsObj.primaryIdx).attr('checked', 'checked');
                }
                else    //JSON is empty
                {
                    $('#saveData').empty();
                    $('#sortable').empty();
                    $('#caption').empty();
                    $('#sorting').empty();
                    $('#caption').append('<b>No data in Media Manager for '+ search_by + ' = ' +
                        ' '+ recordsObj.search_id +'</b>');
                }
            }
            
            // for displaying the busy gif when search button is clicked
            function showBusy()
            {
                $("#busy_icon").show();
            }
            
            // for dynamically changing the position of busy icon when scrolling
            $(window).scroll(function() {
                $('#busy_icon').css('top', $(window).scrollTop() + 'px')
            });
            
            /* onload function executed when window is loaded for the first time */
            /*window.onload = function(){*/

        </script>
    </head>
    <body>
        <!--p>Search by <!--jsp:getProperty name="searchbean" property="search_by" /></p>
        <p>Search id = <!--jsp:getProperty name="searchbean" property="search_id" /></p>
        <p>JSON Object = <!--jsp:getProperty name="searchbean" property="JSON" /></p-->        

        <div id="busy_icon" class="busy_icon" style="display:none">
            <img src="./icons/ajax-loader-circle.gif"/>
        </div>

        <form id="getData" action="search.jsp" method="POST">
            <span id="big">Search</span> for an object: 
            <select id="searchBy" name="search_by">
                <option value="bibid">Orbis Bib ID</option>
                <option value="objectid">TMS Object ID</option>
            </select>
            # <input type="text" id="searchID" name="search_id"/> 
            filename contains (not used if blank): <input type="text" id="pub_onlyID" name="pub_only" value="pub"/>
            <!-- <input type="button" value="Search" onclick="getImagesJSON()"></input> -->
            <input type="submit" value="Search" onclick="showBusy()"></input>
        </form>

        <p id="caption" />

        <div id="sorting">
        <hr/>
        <form>
            <label><span id="big">Sorting</span> will NOT save, please save if you sorted manually! Sorted by:</label>
            <select id="sort_by" onChange="sortData($(this).val());">
                <option value="rank">Rank</option>
                <option value="filename">Filename</option>
                <option value="primary">Primary</option>
                <option value="keywords">Keywords</option>
                <option value="descCaption">Caption</option>
            </select>
        </form>
        </div>
        
        <ul id="sortable"></ul>

        <form id="saveData" action="#">
            <div style="clear: both; padding-top:20px;">
                <hr/>
                <input type="button" value="save" onClick="postJson()"></input>
            </div>
        </form>
        <script>
            parseJson(<%=json%>);
        </script>
    </body>
</html>