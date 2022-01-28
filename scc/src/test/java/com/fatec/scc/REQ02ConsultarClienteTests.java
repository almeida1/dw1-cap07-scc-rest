package com.fatec.scc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fatec.scc.model.ApplicationUser;
import com.fatec.scc.model.Cliente;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ02ConsultarClienteTests {
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Test
	void ct01_quando_consulta_todos_retorna_a_lista_de_clientes_cadastrados() {
		//Dado que a consulta tem acesso sem login e existem registros cadastrados no db
		//Quando solicita consulta todos
		ResponseEntity<List<Cliente>> resposta3 = testRestTemplate.exchange("/api/v1/clientes", HttpMethod.GET, null,new ParameterizedTypeReference<List<Cliente>>() {});
		//Entao
		assertTrue(resposta3.getBody().size()>=1);
    	assertEquals("200 OK", resposta3.getStatusCode().toString());
	}
	@Test
	void ct02_quando_consulta_por_id_cadastrado_retorna_detalhes_do_cliente() {
		//Dado que o id do cliente esta cadastrado e o login de usuario esta cadastrado
		//configura o header com username e senha
    	ApplicationUser user = new ApplicationUser();
    	user.setUsername("jose");
    	user.setPassword("123");
    	HttpEntity<ApplicationUser> httpEntity = new HttpEntity<>(user);
      	
    	//tenta autenticar o usuario para obter o token
    	ResponseEntity<String> resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST,  httpEntity, String.class);
    	assertEquals(HttpStatus.OK, resposta1.getStatusCode());
    	//armazena o token no header do post
    	HttpHeaders headers = resposta1.getHeaders();
    	HttpEntity<?> httpEntity2 = new HttpEntity<>(headers);
    	
		//Quando solicita consulta por id
		ResponseEntity<Cliente> resposta2 = testRestTemplate.exchange("/api/v1/clientes/id/{id}", HttpMethod.GET, httpEntity2, Cliente.class, 1L);
		//Entao retorna os detalhes do cliente
		
    	assertEquals("200 OK", resposta2.getStatusCode().toString());
    	assertNotNull(resposta2.getBody());
    	assertEquals ("Miguel Soares", resposta2.getBody().getNome());
	}
	@Test
	void ct03_quando_consulta_por_cpf_cadastrado_retorna_detalhes_do_cliente() {
		//Dado que o id de cliente esta cadastrado e o login foi realizado com sucesso
		//cadastra um novo usuario da aplicacao
    	ApplicationUser user = new ApplicationUser();
    	user.setUsername("maria");
    	user.setPassword("456");
    	HttpEntity<ApplicationUser> httpEntity = new HttpEntity<>(user);
    	ResponseEntity<String> resposta1 = testRestTemplate.exchange("/users/sign-up", HttpMethod.POST, httpEntity, String.class);
    	
    	//tenta autenticar o usuario para obter o token
    	resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST,  httpEntity, String.class);
    	assertEquals(HttpStatus.OK, resposta1.getStatusCode());
    	//armazena o token no header do post
    	HttpHeaders headers = resposta1.getHeaders();
    	HttpEntity<?> httpEntity2 = new HttpEntity<>(headers);
    	
		//Quando solicita consulta por cpf
		ResponseEntity<Cliente> resposta2 = testRestTemplate.exchange("/api/v1/clientes/{cpf}", HttpMethod.GET, httpEntity2, Cliente.class, "99504993052");
		//Entao retorna os detalhes do cliente
		
    	assertEquals("200 OK", resposta2.getStatusCode().toString());
    	assertNotNull(resposta2.getBody());
    	assertEquals ("Miguel Soares", resposta2.getBody().getNome());
	}
}
