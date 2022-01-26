package com.fatec.scc.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	Optional<Cliente> findByCpf(String cpf);
	@Transactional //
	@Modifying //é usado para aprimorar a anotação @Query para executar não apenas consultas SELECT, 
	           //mas também INSERT, UPDATE, DELETE e até mesmo consultas DDL.
	@Query("DELETE FROM Cliente WHERE cpf = :cpf") //permite escrever querys mais especificas
	void deleteByCpf(@Param("cpf") String cpf);
}
