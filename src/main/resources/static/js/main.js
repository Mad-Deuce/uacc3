// import {EventBus} from './EventBus.js';
let uri='dev';
let templateText;
let contentData;

$(document).ready(function () {

    EventBus.subscribe('getTemplate-OK', function () {
        console.log("getTemplate-OK")
        getContent(uri);
    })
    EventBus.subscribe('getContent-OK', function () {
        console.log("getContent-OK")
        outputHTML();
    })
    getTemplate(uri);

    }
)

// underscore template loader
function getTemplate(templateName) {
    let tmpl_url = '/tmpl/tmpl_' + templateName + '.html';

    $.ajax({
        url: tmpl_url,
        method: 'GET',
        async: false,
        contentType: 'text',
        success: function (tmpl_text) {
            templateText = tmpl_text;
            EventBus.publish('getTemplate-OK');
        }
    });
}

// content loader
function getContent(templateName) {
    let content_url = '/' + templateName + '/';

    $.ajax({
        url: content_url,
        method: 'GET',
        async: false,
        contentType: 'json',
        success: function (content_data) {
            contentData=content_data;
            EventBus.publish('getContent-OK');
        }
    });
}

function outputHTML(){
    let fn = _.template(templateText);
    let value = fn(contentData);
    $("#output").html(value);
}
