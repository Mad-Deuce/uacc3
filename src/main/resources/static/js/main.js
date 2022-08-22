import {EventBus} from './EventBus.js';

let uri='devs';
let templateText;
let contentData;
const CH_NAME_GETTMPLOK='getTemplate-OK';
const CH_NAME_GETCONTOK='getContent-OK';

$(document).ready(function () {

    EventBus.subscribe(CH_NAME_GETTMPLOK, function (param) {
        console.log("getTemplate-OK");
        templateText = param.tmplText;
        getContent(uri);
    })
    EventBus.subscribe(CH_NAME_GETCONTOK, function (param) {
        console.log("getContent-OK");
        contentData=param.contentData;
        outputHTML();
    })
    getTemplate(uri);

    }
)

// underscore template loader
function getTemplate(templateName) {
    let tmplURL = '/tmpl/tmpl_' + templateName + '.html';

    $.ajax({
        url: tmplURL,
        method: 'GET',
        async: false,
        contentType: 'text',
        success: function (tmplText) {
            EventBus.publish(CH_NAME_GETTMPLOK, {tmplText: tmplText});
        }
    });
}

// content loader
function getContent(templateName) {
    let contentURL = '/api/' + templateName + '/';

    $.ajax({
        url: contentURL,
        method: 'GET',
        async: false,
        contentType: 'json',
        success: function (contentData) {
            EventBus.publish(CH_NAME_GETCONTOK, {contentData: contentData});
        }
    });
}

function outputHTML(){
    let fn = _.template(templateText);
    let value = fn(contentData);
    $("#output").html(value);
}
