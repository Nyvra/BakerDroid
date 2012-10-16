/*
 * Main JS
 */

$(function(){

	/*
	scrool funcionando
	
	
	var touchStart= 0;
	var touchEnd= 0;

	$(document).bind("touchstart", function(e) {
		var orig = e.originalEvent;  
		var y = orig.changedTouches[0].pageY;
		touchStart= y;
	});  
	
	$(document).bind("touchend", function(e) {
		var orig = e.originalEvent;
		var y = orig.changedTouches[0].pageY;  
		touchEnd=y;
		
		if(touchStart > touchEnd) {
			//if(!$("#toolbar").hasClass("hidden")) {
			$("#toolbar").css("-webkit-transform", "translate3d(0,-75px,0)");
			//}
		} else if(touchStart < touchEnd) {
			//if($("#toolbar").hasClass("hidden")) {
				//$("#toolbar").css({top:"0"});
				$("#toolbar").css("-webkit-transform", "translate3d(0,0,0)");
			//}
		}
	}); */


	//hide menu highlight
	if($(".main-edit-menu")) $(".main-edit-menu").hide();
	
	$(document).bind("touchend", function() {
		if($(".main-edit-menu")) $(".main-edit-menu").fadeOut();
	});
	
	$(".main-edit-menu, .main-edit-menu span, highlight").bind("touchend", function(event){
		event.stopPropagation();
	});
	
	
	//..
	$(".top").click(function(){
		/*var sel = window.getSelection();
		if (!sel.isCollapsed) {
			var selRange = sel.getRangeAt(0);
			document.designMode = "on";
			sel.removeAllRanges();
			sel.addRange(selRange);
			document.execCommand("HiliteColor", false, "#ffffcc");
			sel.removeAllRanges();
			document.designMode = "off";
		}
		console.log("marca");*/


	/*
		var selectedText = "";
		function getTextSelection() {
			var text = window.getSelection();
			selectedText = text.anchorNode.textContent.substr(text.anchorOffset, text.focusOffset - text.anchorOffset);
		}
		
		alert(getTextSelection());
	*/
		
		//var text = window.getSelection();
		//var txt = window.getSelection();
		//alert(txt.anchorOffset + '|' + txt.anchorNode.textContent + '|' + txt.focusOffset + '|' + txt.focusNode.textContent);
		//alert(text.anchorNode.textContent.substr(text.anchorOffset, text.focusOffset - text.anchorOffset));
		
		//return false;
	});
	
	/*
	 *
	 *	Response implemantation
	 *
	 */
	//var respSize	= $('.response').size();
	//var annotSize = $('.annotations').size();
	var lessonId	= $('body').attr("rel");
	
	//get response if exists
	$('.response').each(function(index) {
		var index = index + 1;
		var key	  = lessonId+"-"+index;
		var val	  = window.localStorage.getItem(key);

		$(this).attr("rel", key);
		
		if(val != "") {
			$(this).val(val);
		}
	});
	
	//get annotations if exists
	$('.annotations').each(function(index) {
		var index = index + 1;
		var key	  = "an"+lessonId+"-"+index;
		var val	  = window.localStorage.getItem(key);

		$(this).attr("rel", key);
		
		if(val != "") {
			$(this).val(val);
		}
	});
	
	//Save response on localstorage
	$('.response, .annotations').blur(function(){		
		var key	= $(this).attr("rel");
		var val	= $(this).val();
		
		window.localStorage.setItem(key, val);
	});
	
	//autoresize text-area
	$('.response, .annotations').autoResize();

	
	/*
	 *
	 *	Link verse
	 *
	 */
	function openVerse($this, show) {
		var bh = $("body").height();
		
		var $this = $this;
		var offset = $this.offset();
		var ov = $(".overlay");
		var vm = $(".verse-main");
		var vl = $(".verse-label");
		
		if(show) {
			ov.css({height: bh}).show();
			vm.css({top: offset.top}).show();
			
			setTimeout(function(){vm.addClass("show-effect")}, 10);
		} else {
			ov.css({height: bh});
			vm.css({top: offset.top});
		}
		vl.css({left: offset.left});
	}
	
	//Click verse
    $(".overlay").click(function(){
		//$(this).fadeOut();
		//$(".verse-main").fadeOut();
		$(".verse-main, .overlay").hide();
		$(".verse-main").removeClass("show-effect");
		$(".lnk").removeClass("open-verse");
	});
	
	//
    //$('.lnk').click(function(){
    //$(".lnk").bind("touchstart click", function(){
    /*$("p").delegate("a", "click", function(event){
		event.stopPropagation();
	});*/
	
	
    $(".lnk").live("click", function(e){
    //$(".lnk").on("click", function(e){
		//event.stopPropagation();
		
		var tx = $(this).attr("title");		
		$(this).addClass("open-verse");		
		$(".verse-text").html(tx);	
		openVerse($(this), 1);
		
		e.stopPropagation();
		return false;
	});
	
	//on resize screen
	$(window).resize(function() {
		if($(".lnk").hasClass("open-verse")) {
			openVerse($(".open-verse"));
		}
	});


	//comments EGW
    $('.eg-comments .label').click(function(){
        var p = $(this).parent();
		//$('div').removeClass("show-comment");
        $('div',p).toggleClass("show-comment");
    });
	
	$('a.days-link').click(function(){
		//$("#toolbar").css("-webkit-transform", "translate3d(0,-75px,0)");
		
		$('div').removeClass("show-comment");
		//$('.dossier-start').hide();
		$('.top').removeClass("d-show");
		

		/*setTimeout(function(){
			// do something
			$('.dossier-start').fadeIn();
		},200);*/
	});
	
	$(window).scroll(function () { 
		  //$("span").css("display", "inline").fadeOut("slow"); 
		  //$('.dossier-start').show();
		  
		  //setTimeout(function(){
			// do something
			$('.top').addClass("d-show");
			//$('.dossier-start').fadeIn();
		//}, 500);
	 });
	 
	 
	 
	$('.info').click(function(){
		$('#info div').toggleClass("show-comment");
    });
	$('.aux').click(function(){
		$('#aux div').toggleClass("show-comment");
    });
	
	/*
	 *
	 *	Video
	 *
	 */
	$(".play").click(function(){
		//window.location.hash = "#sab";
		
		if(!$(this).hasClass("close")) {
			$(this).addClass("close");
			$(".img-top img").hide();
			$(".main-video").show();
			
			var v = $(".main-video")[0];
			v.play(); 
		} else {
			
			var v = $(".main-video")[0];
			v.pause(); 
			
			$(".main-video").hide();
			$(".img-top img").show();
			$(this).removeClass("close");
		}
		
		//$("#toolbar").css("-webkit-transform", "translate3d(0,-75px,0)");
    });
});