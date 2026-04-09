package com.sistema.tradiciones.repository;

import com.sistema.tradiciones.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Para verificar si el cliente ya existe antes de crearlo
    Optional<Cliente> findByCedula(String cedula);
}