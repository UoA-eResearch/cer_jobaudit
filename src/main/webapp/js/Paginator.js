function Paginator(divId, pageCount, onPageChangeCallback) {

    /* div where the paginator is rendered */
    this.divId = divId;
    /* number of pages */
    this.pageCount = pageCount;
    /* Callback being invoked when the page changes */
    this.onPageChangeCallback = onPageChangeCallback;

    Paginator.prototype.render = function() {
    	$(divId).paging(this.pageCount, {
    	    format: "[ < > ] . (qq -) nnnnncnnnnn (- pp)",
    	    perpage: 1, lapping: 1, page: 1,
    	    onSelect: function(page) { onPageChangeCallback(page); } ,
    	    onFormat: function(type) { 
    	        switch (type) {
    	            case 'block':
    	                if (!this.active) {
    	                    return '<span class="disabled">' + this.value + '</span>';
    	                } else if (this.value != this.page) {
    	                    return '<em><a href="#' + this.value + '">' + this.value + '</a></em>';
    	                }
    	                return '<span class="current">' + this.value + '</span>';
    	            case 'next':
    	                if (this.active) {
    	                    return '<a href="#' + this.value + '" class="next">&gt;</a>';
    	                }
    	                return '<span class="disabled">&gt;</span>';
    	            case 'prev':
    	                if (this.active) {
    	                    return '<a href="#' + this.value + '" class="prev">&lt;</a>';
    	                }
    	                return '<span class="disabled">&lt;</span>';
    	            case 'first':
    	                if (this.active) {
    	                    return '<a href="#' + this.value + '" class="first">First page</a>';
    	                }
    	                return '<span class="disabled">First page</span>';
    	            case 'last':
    	                if (this.active) {
    	                    return '<a href="#' + this.value + '" class="last">Last page</a>';
    	                }
    	                return '<span class="disabled">Last page</span>';
    	            case 'leap':
    	                if (this.active) {
    	                    return "   ";
    	                }
    	                return "";
    	            case 'fill':
    	                if (this.active) {
    	                    return "... ";
    	                }
    	                return "";
    	            case 'left':
    	                if (this.active) {
    	                    return '<em><a href="#' + this.value + '">' + this.value + '</a></em>';
    	                }
    	                return "";
    	            case 'right':
    	                if (this.active) {
    	                    return '<em><a href="#' + this.value + '">' + this.value + '</a></em>';
    	                }
    	                return "";
    	        }
    	    }
    	});
    }
}