import {EventBus} from './EventBus.js';

let uri = 'devs';
let templateText;
let contentData;

let requestData = '';
let requestDTO = {
    id: "",
    grid: ""
};
// let requestDTO='filter.id=1544552';


let ascDirection = "asc";

const CH_NAME_GETTMPLOK = 'getTemplate-OK';
const CH_NAME_GETCONTOK = 'getContent-OK';

$(document).ready(function () {
        EventBus.subscribe(CH_NAME_GETTMPLOK, function (param) {
            console.log(CH_NAME_GETTMPLOK);
            templateText = param.tmplText;
            getContent(uri);
        })
        EventBus.subscribe(CH_NAME_GETCONTOK, function (param) {
            console.log(CH_NAME_GETCONTOK);
            contentData = param.contentData;
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
        // url: '/api/devs/?filter.id=1544552',
        method: 'GET',
        data: requestDTO,
        async: false,
        contentType: 'json',
        traditional: false,
        success: function (contentData) {
            EventBus.publish(CH_NAME_GETCONTOK, {contentData: contentData});
        }
    });
}

//rendering template
function outputHTML() {
    let fn = _.template(templateText);
    let value = fn(contentData);
    $("#content").html(value);
}

//Events block
$("#content").on("click", "#sort_byId", function () {
    ascDirection = (ascDirection === 'asc' ? 'desc' : 'asc');
    requestData = 'sort=id,' + ascDirection;
    getContent(uri);
})

$("#content").on("change", "#filter_byId", function (param) {
    requestDTO.id = $("#filter_byId").val();
    getContent(uri);
})

$("#content").on("change", "#filter_byGrid", function (param) {
    requestDTO.grid = $("#filter_byGrid").val();
    getContent(uri);
})
