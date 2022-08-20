$(document).ready(function(){
    var template_text = $("#tmpl_ListItem").html();
    let fn = _.template(template_text);

    $.ajax({
        url: 'http://192.168.1.245:8080/dev/97459',
        method: 'GET',
        async: false,
        contentType: 'json',
        success: function (template_data) {
            let value = fn(template_data);
            $("#lines").html(value);
        }
    });
})