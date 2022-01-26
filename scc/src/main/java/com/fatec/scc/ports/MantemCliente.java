package com.fatec.scc.ports;
import java.util.List;
import java.util.Optional;

import com.fatec.scc.model.Cliente;

public interface MantemCliente {
	
	List<Cliente> consultaTodos();
	Optional<Cliente> consultaPorCpf(String cpf);
	Optional<Cliente> consultaPorId(Long id);
	Cliente save(Cliente cliente);
	void delete (Long id);
	Cliente altera ( Cliente cliente);

}