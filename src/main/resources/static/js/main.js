import {EventBus} from './EventBus.js';

let uri = 'devs';
let templateText;
let contentData;
let isUpdate = false;

// let requestData = '';
let requestDTO = {
    //data
    id: "",
    grid: "",
    //sort
    sort: "",
    //pagination
    size: "30",
    page: "1"
};


// let ascDirection = "asc";

const CH_NAME_GETTMPLOK = 'getTemplate-OK';
const CH_NAME_GETCONTOK = 'getContent-OK';


$(document).ready(function () {

        EventBus.subscribe(CH_NAME_GETTMPLOK, function (param) {
            // console.log(CH_NAME_GETTMPLOK);
            templateText = param.tmplText;
            getContent(uri);
        })

        EventBus.subscribe(CH_NAME_GETCONTOK, function (param) {
            // console.log(CH_NAME_GETCONTOK);
            contentData = param.contentData;
            if (isUpdate) {
                updateHTML();
            } else {
                outputHTML();
            }
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
    let value = fn({contentData});
    $("#content").html(value);
}

//update contentData
function updateHTML(){
    let regExpForSub = /<!--start_sub_template-->.*<!--end_sub_template-->/gms;
    let subTemplateText = templateText.match(regExpForSub);
    let subFn = _.template(subTemplateText[0]);
    let subValue = subFn({contentData});
    $("#sub_template").html(subValue);
}

//Events block for $("#content")
let contentElement = $("#content");

contentElement.on("click", "#sort_byId", function () {
    requestDTO.sort = (requestDTO.sort === 'id,asc' ? 'id,desc' : 'id,asc');
    isUpdate = true;
    getContent(uri);
})

contentElement.on("click", "#sort_byGrid", function () {
    requestDTO.sort = (requestDTO.sort === 'sDev.grid,asc' ? 'sDev.grid,desc' : 'sDev.grid,asc');
    isUpdate = true;
    getContent(uri);
})

contentElement.on("input", "#filter_byId", function () {
    requestDTO.id = $("#filter_byId").val();
    isUpdate = true;
    getContent(uri);
})

contentElement.on("input", "#filter_byGrid", function (param) {
    requestDTO.grid = $("#filter_byGrid").val();
    isUpdate = true;
    getContent(uri);
})

contentElement.on("change", "#item_on_page_input", function (param) {

    requestDTO.size = $("#item_on_page_input").val();
    isUpdate = true;
    document.getElementById("item_on_page_output").textContent = "Items on page " + $("#item_on_page_input").val();
    getContent(uri);

})