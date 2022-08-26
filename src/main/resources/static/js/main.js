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
    page: ""
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
function updateHTML() {
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

contentElement.on("change", "#range_item_on_page", function (param) {
    requestDTO.page="0";
    // contentData.number=0;
    requestDTO.size = $("#range_item_on_page").val();
    isUpdate = true;
    getContent(uri);

    document.getElementById("item_on_page_output").textContent = "Items on page " + $("#range_item_on_page").val();
    document.getElementById("input_find_page").value = (contentData.number + 1) + " out of " + contentData.totalPages;
})

contentElement.on("keypress", "#input_find_page", function (param) {
    if (param.which === 13) {           //13 - это код клавиши "Enter"
        if (!isNaN($("#input_find_page").val())) {
            requestDTO.page = $("#input_find_page").val() - 1;
            isUpdate = true;
            getContent(uri);
        }
        document.getElementById("input_find_page").blur();
        document.getElementById("input_find_page").value = (contentData.number + 1) + " out of " + contentData.totalPages;
    }
})

contentElement.on("click", "#btn_first_page", function () {
    requestDTO.page = "0";
    isUpdate = true;
    getContent(uri);
    document.getElementById("input_find_page").value = (contentData.number + 1) + " out of " + contentData.totalPages;
})
contentElement.on("click", "#btn_last_page", function () {
    requestDTO.page = contentData.totalPages - 1;
    isUpdate = true;
    getContent(uri);
    document.getElementById("input_find_page").value = (contentData.number + 1) + " out of " + contentData.totalPages;
})

contentElement.on("click", "#btn_prev_page", function () {
    // requestDTO.page = "0";
    requestDTO.page = (contentData.number <= 0 ? 0 : contentData.number - 1);
    isUpdate = true;
    getContent(uri);
    document.getElementById("input_find_page").value = (contentData.number + 1) + " out of " + contentData.totalPages;
})
contentElement.on("click", "#btn_next_page", function () {
    // requestDTO.page = "0";
    requestDTO.page = (contentData.number >= contentData.totalPages - 1 ? contentData.totalPages - 1 : contentData.number + 1);
    isUpdate = true;
    getContent(uri);
    document.getElementById("input_find_page").value = (contentData.number + 1) + " out of " + contentData.totalPages;
})
