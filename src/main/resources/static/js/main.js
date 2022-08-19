$(function(){

    var lines = [],
        template = $("#tmpl_ListItem").html();

    lines.push({
            id : 1,
            text : 'Test1',
            done : false
        },
        {
            id : 2,
            text : 'Test2',
            done : true
        },
        {
            id : 3,
            text : 'Test3',
            done : false
        });

    let fn = _.template(template);
    console.log(fn);

    let value = fn({items:lines});
    $("#lines").html(value);

});