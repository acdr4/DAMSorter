<%-- 
    Document   : search
    Created on : May 27, 2013, 3:47:23 PM
    Author     : acdr4
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta charset="utf-8" />
        <title>jQuery UI Sortable - Display as grid</title>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
        <script src="http://code.jquery.com/jquery-1.8.2.js"></script>
        <script src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
        <style>
            #sortable { list-style-type: none; margin: 0; padding: 0; width: 100%; height: 100%;}
            #sortable li { margin: 3px 3px 3px 0; padding: 1px; float: left; width: 128px; height: 100%; font-weight: bold; font-size: 1em; text-align: left; }
            #transbox { background-color:#ffffff; color:#000; width:20px; border:1px solid black; opacity:0.4; filter:alpha(opacity=40); } 
            img {max-width:128px;max-height:128px;}
            #saveData {max-height:100%; display: inline;}
        </style>

        <!--create a useBean session that will fetch JSON for the searched object -->
        <jsp:useBean id="searchbean" scope="session" class="edu.yale.damsorter.SearchQueryHandler" />
        <jsp:setProperty name="searchbean" property="search_by" />
        <jsp:setProperty name="searchbean" property="search_id" />
        <%
            searchbean.path = pageContext.getServletContext().getRealPath("/");
            searchbean.url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            String json = searchbean.getJSON();
        %>

        <script>

            //var old_position;
            var new_position = new Array();
            var recordsObj;
    
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
                recordsObj.primaryIdx = index;
                //displayRecordsObj("primaryIdx");
            }
            
            //sets cds level of the object with index provided
            function setCDSLevel(index)
            {
                recordsObj.recordsArr[index].cdsLevel = $('#cds_level'+index+'').val();
                //displayRecordsObj("cdsLevel");
            }
        
            //update all the ranks as per the newly sorted objects
            function updateRank(posArr)
            {
                var rankArr = new Array(posArr.length);
                for(i = 0; i < posArr.length; i++)
                {
                    rankArr[posArr[i]] = i;
                }
                $.each(recordsObj.recordsArr, function(i) {
                    recordsObj.recordsArr[i].rank = rankArr[i];
                });
            }
            
            //helper function to display various contents of recordsObj JSON object
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
                else if(param == "primaryIdx"){
                    temp.push(recordsObj.primaryIdx);
                }
                else if(param == "cdsLevel"){
                    for(i = 0; i < recordsObj.recordsArr.length; i++){
                        temp.push(recordsObj.recordsArr[i].cdsLevel);
                    }
                }
                alert(temp);
            }
            
            //executed when save button is clicked...posts recordsObj to save.jsp
            function postJson()
            {
                // confirm window pops up for safety before data is written in DAM
                var check = confirm("Changes will be written to DAM. Press OK to proceed.");
                if (check == true)
                {
                    $.ajax({
                        url:    "save.jsp",
                        type:   "post",
                        data:   "json_data="+JSON.stringify(recordsObj),
                        success: function(){
                            alert("Changes successfully posted to DAM!");
                        },
                        error: function() {
                            alert("ERROR: Failed to write to DAM.");
                        }
                    });
                }                
            }
            
            //extract JSON data and display
            function parseJson(json){
                recordsObj = json;
                var search_by = "";
                if(recordsObj.search_by == "bibid")
                    search_by = "Orbis Bib ID = "
                else
                    search_by = "TMS Object ID = "
                //parse JSON only if it's not empty
                if (recordsObj.recordsArr.length > 0)
                {
                    $('#caption').empty();
                    $('#caption').append('<b>Displaying search results for '+ search_by +
                        ' '+ recordsObj.search_id +'</b>');
                    $('#sortable').empty();
                    $('#sortable').append('<hr/>');
                    //set up each image div from json
                    $.each(recordsObj.recordsArr, function(i) {
                        $('#sortable').append('<li class="ui-state-default" id="'+i+'">'+
                            '<div onClick="showData(\''+
                            recordsObj.recordsArr[i].id+
                            '\');"><img alt="'+
                            recordsObj.recordsArr[i].id+
                            '" src="'+recordsObj.recordsArr[i].thumb+
                            '"></div><div><input id = "is_primary'+i+
                            '" type="radio" name="primary" value="primary"'+
                            'onClick = setPrimary('+i+')>primary</div>'+
                            'CDS Level:<select id="cds_level'+i+
                            '" onchange = setCDSLevel('+i+')>'+
                            '<option value="11">11</option>'+
                            '<option value="12">12</option></select></li>');
                        //set the appropriate default cds level
                        $('#cds_level'+i).val(recordsObj.recordsArr[i].cdsLevel);
                        //initialize positions array with default ranks i.e 0,1,2,3...
                        new_position.push(i);
                    });
                    //check the appropriate primary radio button
                    if(recordsObj.primaryIdx >= 0)
                        $("#is_primary"+recordsObj.primaryIdx).attr('checked', 'checked');
                }
                else    //JSON is empty
                {
                    $('#saveData').empty();
                    $('#sortable').empty();
                    $('#caption').empty();
                    $('#caption').append('<b>No data in Media Manager for '+ search_by +
                        ' '+ recordsObj.search_id +'</b>');
                }
            }
            
            /* onload function executed when window is loaded for the first time */
            /*window.onload = function(){*/

        </script>
    </head>
    <body>
        <!--p>Search by <!--jsp:getProperty name="searchbean" property="search_by" /></p>
        <p>Search id = <!--jsp:getProperty name="searchbean" property="search_id" /></p>
        <p>JSON Object = <!--jsp:getProperty name="searchbean" property="JSON" /></p-->        

        <form id="getData" action="search.jsp" method="POST">
            Search for an object: 
            <select name="search_by">
                <option value="bibid">Orbis Bib ID</option>
                <option value="objectid">TMS Object ID</option>
            </select>
            # <input type="text" name="search_id"/>            
            <!-- <input type="button" value="Search" onclick="getImagesJSON()"></input> -->
            <input type="submit" value="Search"></input>
        </form>

        <p id="caption" />

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