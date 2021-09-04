package com.c4_soft.starter.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.transaction.PlatformTransactionManager;

import com.c4_soft.springaddons.security.oauth2.test.mockmvc.JwtTestConf;
import com.c4_soft.springaddons.security.oauth2.test.mockmvc.MockMvcSupport;
import com.c4_soft.springaddons.test.support.web.SerializationHelper;
import com.c4_soft.starter.cafeskifo.persistence.SecuredOrderRepo;

@WebMvcTest(OrdersController.class)
@Import({ MockMvcSupport.class, SerializationHelper.class, JwtTestConf.class })
@TypeHint(types = MockMvcSupport.class, access = AccessBits.ALL)
class OrdersControllerTest {

	@MockBean
	PlatformTransactionManager transactionManager;

	@MockBean
	SecuredOrderRepo orderRepo;

	@Autowired
	MockMvcSupport mockMvc;

}
