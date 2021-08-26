package com.c4_soft.starter.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.hint.TypeHint;

import com.c4_soft.springaddons.security.oauth2.test.mockmvc.MockMvcSupport;
import com.c4_soft.springaddons.test.support.web.SerializationHelper;

@WebMvcTest(OrdersController.class)
@Import({ MockMvcSupport.class, SerializationHelper.class })
@TypeHint(types = MockMvcSupport.class, access = AccessBits.ALL)
class OrdersControllerTest {

	@Autowired
	MockMvcSupport mockMvcSupport;

	MockMvcSupport api() {
		return mockMvcSupport.with((var request) -> {
			request.setSecure(true);
			return request;
		});
	}

}
