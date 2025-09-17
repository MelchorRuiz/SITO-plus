package com.mycompany.recursos.humanos.service.service;

import com.mycompany.recursos.humanos.service.model.Profesor;
import com.mycompany.recursos.humanos.service.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfesorService {

    @Autowired
    private ProfesorRepository repository;

    // Crear profesor
    public Profesor crearProfesor(Profesor p) {
        return repository.save(p);
    }

    // Listar todos
    public List<Profesor> obtenerTodos() {
        return repository.findAll();
    }

    // Obtener por ID
    public Profesor obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
    }

    // Actualizar profesor
    public void actualizarProfesor(Long id, Profesor p) {
        Profesor existente = obtenerPorId(id);
        existente.setNombre(p.getNombre());
        existente.setApellido(p.getApellido());
        existente.setMateria(p.getMateria());
        existente.setSalario(p.getSalario());
        existente.setPuesto(p.getPuesto());
        repository.save(existente);
    }

    // Cambiar contraseña
    public void cambiarContrasena(Long id, String nueva) {
        Profesor existente = obtenerPorId(id);
        existente.setHashedPassword(nueva); // Si quieres, aquí puedes encriptarla
        repository.save(existente);
    }

    // Eliminar profesor
    public void eliminarProfesor(Long id) {
        repository.deleteById(id);
    }
}
