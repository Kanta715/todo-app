//  背景色、文字の変更をするため、slugクラスの要素を全て取得
let slug = document.getElementsByClassName("slug");

/*  for文で、各要素に、背景色と文字の変換をする
  今回は特に指定がなかったので、下記のように色を指定しています
  フロントエンド -> 赤系、    バックエンド -> 青系、   インフラ -> 緑系
*/
for(let i = 0;  i <= slug.length;   i++){
    switch(slug[i].textContent){
        case    "1":
            slug[i].style.backgroundColor = "tomato";
            slug[i].innerHTML             = "フロントエンド";
            break;
        case    "2":
            slug[i].style.backgroundColor = "royalblue";
            slug[i].innerHTML             = "バックエンド";
            break;
        case    "3":
            slug[i].style.backgroundColor = "teal";
            slug[i].innerHTML             = "インフラ";
            break;
    }
}