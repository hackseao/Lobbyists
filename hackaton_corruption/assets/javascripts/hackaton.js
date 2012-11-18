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

    wa.addClass('panel').html('<h1>Results : ' + results.length+'</h1>').css('overflow', 'scroll');

    for (i = 0; i < results.length; i++) {
        source = results[i];
        
            if(!indexes[source._index]){
                indexes[source._index] = 0;
            }
            
            indexes[source._index]++;
    }
    console.log(indexes);
    for (var key in indexes){
        size = 1 +  indexes[key] * 0.01;
        wa.append('<p class="'+key+'" style="font-size:'+ size +'em">'+key+': '+indexes[key] +'</p>');
        wa.find('p').click(generate_table_from_results(wa, results));
    }
    
    wa.append('<table></table>');
    
    
}

function generate_table_from_results(item, results){
    return function(e){
        var first = true;

        var index = this.className;
        
        table = item.find('table');
        table.html('');
        table.append('<tr></tr>');

        for (i = 0; i < results.length; i++) {
            if(results[i]._index == index){

                source = results[i]._source;
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
}
