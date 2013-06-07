<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>jQuery UI Sortable - Display as grid</title>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
        <script src="http://code.jquery.com/jquery-1.8.2.js"></script>
        <script src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
    </head>

    <style>
        #busy_icon {position: absolute; left: 45%; top: 48%;}
    </style>
    
    <script>
        // for displaying the busy gif when search button is clicked
        function showBusy()
        {
            $("#busy_icon").show();
        }
    </script>

    <body>
        
        <div id="busy_icon" class="busy_icon" style="display:none">
            <img src="./icons/ajax-loader-circle.gif"/>
        </div>

        <form id="getData" action="search.jsp" method="POST">
            Search for an object: 
            <select name="search_by">
                <option value="bibid">Orbis Bib ID</option>
                <option value="objectid">TMS Object ID</option>
            </select>
            # <input type="text" name="search_id"/>            
            <!-- <input type="button" value="Search" onclick="getImagesJSON()"></input> -->
            <input type="submit" value="Search" onclick="showBusy()"></input>
        </form>
    </body>
</html>