function signup() {
    let firstname = document.getElementById("firstname").value;
    let lastname = document.getElementById("lastname").value;
    let username = document.getElementById("username").value;
    let email = document.getElementById("signupemail").value;
    let password = document.getElementById("signuppassword").value;

    let data = {
        "username": username,
        "firstname": firstname,
        "lastname": lastname,
        "email": email,
        "password": password
    };

    fetch("http://localhost:8080/signup", {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Origin": "http://localhost:8080/"
        },
        mode: "cors"
    }).then(res => res.json())
        .then(function (json) {
        console.log(json);
    });
}

function login() {
    let email = document.getElementById("loginemail").value;
    let password = document.getElementById("loginpassword").value;

    let data = {
        "email": email,
        "password": password
    };

    fetch("http://localhost:8080/login", {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Origin": "http://localhost:8080/"
        },
        mode: "cors"
    }).then(res => res.json())
     .then(function(json){
        console.log(json);
    });
}

function creategroup() {
    let groupname = document.getElementById("creategroupname").value;
    // let password = document.getElementById("creategroup").value;

    let data = {
        "name": groupname
    };

    fetch("http://localhost:8080/groups/create", {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Origin": "http://localhost:8080/"
        },
        mode: "cors"
    }).then(res => res.json())
     .then(function(json){
        console.log(json);
    });
}

function getlist() {
    fetch("http://localhost:8080/groups/list", {
        method: "GET",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Origin": "http://localhost:8080/"
        },
        mode: "cors"
    }).then(res => res.json())
     .then(function(json){
        console.log(json);
    });
}

function getmessages() {
    let groupname = document.getElementById("messagesgroupname").value;
    fetch("http://localhost:8080/groups/" + groupname + "/messages", {
        method: "GET",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Origin": "http://localhost:8080/"
        },
        mode: "cors"
    }).then(res => res.json())
     .then(function(json){
        console.log(json);
    });
}

function sendmessage() {
    let groupname = document.getElementById("sendmessagegroupname").value;
    let sendusername = document.getElementById("sendusername").value;
    let message = document.getElementById("sendmessage").value;

    let data = {
            "username": sendusername,
            "message": message
        };


    fetch("http://localhost:8080/groups/" + groupname + "/messages", {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Origin": "http://localhost:8080/"
        },
        mode: "cors"

    });
}