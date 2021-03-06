$( document ).delegate("#chatroom", "pageinit", function() {
	var fixgeometry = function() {
	    /*
		 * Some orientation changes leave the scroll position at something that
		 * isn't 0,0. This is annoying for user experience.
		 */
	    scroll(0, 0);

	    /* Calculate the geometry that our content area should take */
	    var header = $(":jqmData(role=header)");
// var footer = $(".footer:visible");
	    var content = $(":jqmData(role=content)");
	    var viewport_height = $(window).height();
	    
	    var content_height = viewport_height - header.outerHeight(); 
// - footer.outerHeight();
	    
	    /* Trim margin/border/padding height */
	    content_height -= (content.outerHeight() - content.height());
	    content.height(content_height);
	  }; /* fixgeometry */

	  
// $(document).on('pageinit',function() {
	    $(window).bind("orientationchange resize pageshow", fixgeometry);
		 $('#messagebtn').click(function(event){
		  var t = $('#message').val();
		  if(t){
			  $.ajax({url:'/message',type:'post',data:{'c.messageText' : t},success:function(){
				  console.log(t);
				  $('#message').val('');
				  }});
			  }
		  }
		 );
		 function prependMessage(map){
				var _res = map.events;
				var html;
				var m = $("#messages");
				var f = map.f;
				var lastEventId;
				$.each(_res,function(index){
					html = "<p>"+this.data.owner.name+": "+this.data.messageText +"</p>";
					lastEventId = this.id;
					m.prepend(html);
				});
				f(lastEventId);
			}
		 var lastEventSeen = 0;
		 var getMessages = function() {
	             $.ajax(
	            		 {url:'/consume',
	            			 data:{lastEventSeen:lastEventSeen},
	            			 type:'post',
	            			 success:function(events) {
	                        	 //chatroom.html
	            				 prependMessage({events:events, f:function(lastEventId){
	            					 lastEventSeen = lastEventId;
	            					 getMessages();
	            				 }});
	                     }});
	     };
	     getMessages();
// });
});

$( document ).delegate("#chatrooms", "pageinit", function() {
	$('.chatroomlistli').on('click',function(){
		$('#'+$(this).data('formid')).submit();
	});	
});

