let color = document.getElementsByClassName("color");

for(let i = 0;  i <= color.length;   i++){
    switch(color[i].textContent){
        case    "1":
            color[i].style.backgroundColor = "tomato";
            color[i].innerHTML             = "赤";
            break;
        case    "2":
            color[i].style.backgroundColor = "royalblue";
            color[i].innerHTML             = "青";
            break;
        case    "3":
            color[i].style.backgroundColor = "teal";
            color[i].innerHTML             = "緑";
            break;
    }
}

