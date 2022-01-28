package com.fatec.scc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

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

import com.fatec.scc.model.ApplicationUser;
import com.fatec.scc.model.Cliente;
import com.fatec.scc.ports.ClienteRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ01CadastrarUsuarioTests {

	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private ClienteRepository clienteRepository;

	@Test
	void ct01_quando_cadastra_cliente_valido_retorna_clientes_cadastrado() {
		// Dado que o cliente nao esta cadastrado e o login esta cadastrado
		// configura o header com username e senha
		ApplicationUser user = new ApplicationUser();
		user.setUsername("jose");
		user.setPassword("123");
		HttpEntity<ApplicationUser> httpEntity = new HttpEntity<>(user);

		// tenta autenticar o usuario para obter o token
		ResponseEntity<String> resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST, httpEntity, String.class);
		assertEquals(HttpStatus.OK, resposta1.getStatusCode());
		// armazena o token no header 
		HttpHeaders headers = resposta1.getHeaders();
		

		// Quando solicita consulta por id
//		{
//		    "cpf": "99504993052",
//		    "nome": "Jose Antonio",
//		    "email": "jose@gmail.com",
//		    "cep": "04280130"
//		}
		String urlBase = "/api/v1/clientes/";
		//String entityClienteJSon = "{\"cpf\":\"60545111579\", \"nome\":\"Carlos Miranda \", "
		//		+ "\"email\": \"carlos@gmail.com \" , \"cep\": \"04280130 \"  }";
		//Quando solicita o cadastro
		                   
		Cliente cliente = new Cliente("Carlos Miranda","20/10/1961","M", "60545111579",  "04280130", "123");
		HttpEntity<Cliente> httpEntity3 = new HttpEntity<Cliente>(cliente, headers);
		ResponseEntity<String> resposta = testRestTemplate.exchange(urlBase, HttpMethod.POST, httpEntity3, String.class);
		//Entao retorna os detalhes do cliente
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		Optional<Cliente>  re = clienteRepository.findByCpf("60545111579");
		System.out.println (">>>>>>" + re.get().getDataCadastro());
		assertTrue(re.isPresent());
	}
	@Test
	void ct02_quando_cadastra_cliente_ja_cadastrado_retorna_erro() {
		// Dado que o cliente esta cadastrado e o login esta cadastrado
		// configura o header com username e senha
		ApplicationUser user = new ApplicationUser();
		user.setUsername("jose");
		user.setPassword("123");
		HttpEntity<ApplicationUser> httpEntity = new HttpEntity<>(user);

		// tenta autenticar o usuario para obter o token
		ResponseEntity<String> resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST, httpEntity, String.class);
		assertEquals(HttpStatus.OK, resposta1.getStatusCode());
		// armazena o token no header 
		HttpHeaders headers = resposta1.getHeaders();
		

		// Quando solicita consulta por id
//		{
//		    "cpf": "99504993052",
//		    "nome": "Jose Antonio",
//		    "email": "jose@gmail.com",
//		    "cep": "04280130"
//		}
		String urlBase = "/api/v1/clientes/";
		//String entityClienteJSon = "{\"cpf\":\"60545111579\", \"nome\":\"Carlos Miranda \", "
		//		+ "\"email\": \"carlos@gmail.com \" , \"cep\": \"04280130 \"  }";
		//Quando solicita o cadastro
		Cliente cliente = new Cliente("Carlos Miranda","20/10/1961","M", "99504993052",  "04280130", "123");
		HttpEntity<Cliente> httpEntity3 = new HttpEntity<Cliente>(cliente, headers);
		ResponseEntity<String> resposta = testRestTemplate.exchange(urlBase, HttpMethod.POST, httpEntity3, String.class);
		//Entao retorna os detalhes do cliente
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		Optional<Cliente>  re = clienteRepository.findByCpf("60545111579");
		assertFalse(re.isPresent());
	}
}
