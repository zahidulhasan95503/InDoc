//confirm("this is script file");

const toggleSidebar = () => {
    if($(".sidebar").is(":visible")){

        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%")
        $("#mylocation").css("margin-left","190px")
       

    }else{

        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%")
        $("#mylocation").css("margin-left","40px")
        $(".aaddes").css("margin-left","10%")
    }



};

