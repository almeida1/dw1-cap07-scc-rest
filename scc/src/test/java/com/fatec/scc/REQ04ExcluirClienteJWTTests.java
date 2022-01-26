package com.fatec.scc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fatec.scc.security.ApplicationUser;
import com.fatec.scc.model.Cliente;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ04ExcluirClienteJWTTests {
	@Autowired
	private TestRestTemplate testRestTemplate;
   
    @Test
    public void ct01_dado_que_o_id_cliente_existe_na_exclusao_retorna_no_content() throws Exception {
    	//cadastra o login do usuario da aplicacao
    	ApplicationUser user = new ApplicationUser();
    	user.setUsername("jose");
    	user.setPassword("123");
    	HttpEntity<ApplicationUser> httpEntity = new HttpEntity<>(user);
    	//ResponseEntity<String> resposta = testRestTemplate.exchange("/users/sign-up", HttpMethod.POST, httpEntity, String.class);
    	
    	//tenta autenticar o usuario para obter o token
    	ResponseEntity<String> resposta = testRestTemplate.exchange("/login", HttpMethod.POST,  httpEntity, String.class);
    	assertEquals(HttpStatus.OK, resposta.getStatusCode());
    	
    	//armazena o token no header do post
    	HttpHeaders headers = resposta.getHeaders();
    	HttpEntity<?> httpEntity2 = new HttpEntity<>(headers);
    	
    	//solicita a exclusao pelo id enviando o token de autenticacao no header
    	ResponseEntity<Cliente> resposta3 = testRestTemplate.exchange("/api/v1/clientes/id/{id}", HttpMethod.DELETE, httpEntity2,Cliente.class,1);
    	assertEquals("204 NO_CONTENT", resposta3.getStatusCode().toString());
    	
    	
    }
    @Test
    public void ct02_dado_que_a_senha_invalida_retorna_nao_autorizado() throws Exception {
    	//cadastra usuario
    	ApplicationUser user = new ApplicationUser();
    	user.setUsername("maria");
    	user.setPassword("456");
    	HttpEntity<ApplicationUser> httpEntity = new HttpEntity<>(user);
    	ResponseEntity<String> resposta = testRestTemplate.exchange("/users/sign-up", HttpMethod.POST, httpEntity, String.class);
    	
    	//tenta se autenticar para obter o token com a senha errada
    	user = new ApplicationUser();
    	user.setUsername("maria");
    	user.setPassword("1234");
    	HttpEntity<ApplicationUser> httpEntity3 = new HttpEntity<>(user);
    	resposta = testRestTemplate.exchange("/login", HttpMethod.POST,  httpEntity3, String.class);
    	assertEquals(HttpStatus.FORBIDDEN, resposta.getStatusCode());
     }
}

