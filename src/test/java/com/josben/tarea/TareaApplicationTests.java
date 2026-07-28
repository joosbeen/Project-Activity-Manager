package com.josben.tarea;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:tareadb-test;DB_CLOSE_DELAY=-1")
class TareaApplicationTests {

	@Test
	void contextLoads() {
	}

}
