$(document).ready(

function() {
    $('#search').submit(search);
});

function search(e) {
    e.stopPropagation();
    $.getJSON("http://localhost:9200/_search", {
        pretty: true,
        q: $('#q').val(),
        size: 1500
    }, search_response);

    return false;
}

function search_response(data, status) {
    console.log(data);
    var ra = $("#result_area"); // Where we shall draw our canvas
    var wa = false; // The svg we'll be performing on
    var table = false; // Table to use to list the results
    var results = data.hits.hits;
    var row = ''; // String used to create 
    var source = false;
    var index = '';
    var indexes = Array();

    ra.html('<div id="work_area"></div>');

    wa = $('#work_area');

    wa.addClass('panel').text('Results : ' + results.length);

    for (i = 0; i < results.length; i++) {
        source = results[i];
        
            if(!indexes[source._index]){
                indexes[source._index] = 0;
            }
            
            indexes[source._index]++;
    }
    console.log(indexes);
    for (var key in indexes){
        wa.append('<p style="font-size:1.'+indexes[key] +'em">'+key+': '+indexes[key] +'</p>');
    }
    
    
}

function generate_table_from_results(results){
    table = wa.find('table');
    table.append('<tr></tr>');

    for (i = 0; i < results.length; i++) {
        source = results[i]._source;
        if (results[i]._index == 'jdbc') {
            row = '<tr>';
            for (var key in source) {
                if (first) {
                    table.find('tr:first-child').append('<th>' + key + '</th>');
                }
                row += '<td>' + source[key] + '</td>';
            }
            row += '/<tr>';
            table.append(row);
            first = false;
        }
    }
}
