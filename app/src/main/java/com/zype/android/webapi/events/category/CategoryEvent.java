package com.zype.android.webapi.events.category;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.category.CategoryResponse;

public class CategoryEvent extends DataEvent<CategoryResponse> {

    public CategoryEvent(RequestTicket ticket, CategoryResponse data) {
        super(ticket, data);
    }
}
