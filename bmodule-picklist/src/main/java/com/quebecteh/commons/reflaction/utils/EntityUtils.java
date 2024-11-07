package com.quebecteh.commons.reflaction.utils;


import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

public class EntityUtils {

    /**
     * Retorna todas as classes em um dado pacote e seus subpacotes que possuem a anotação @Entity.
     *
     * @param pacote o nome do pacote a ser escaneado
     * @return um array de Class<?> contendo as classes anotadas com @Entity
     */
    public static Class<?>[] getClasses(String pacote) {
        Reflections reflections = new Reflections(pacote, Scanners.TypesAnnotated);

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Entity.class, true);

        return classes.toArray(new Class<?>[0]);
    }
    
 // Método genérico que recebe um objeto T e executa o método anotado com @Id
    public static <T> Optional<Object> getId(T obj) {
        if (obj == null) {
            return Optional.empty();
        }

        // Recupera a classe do objeto
        Class<?> clazz = obj.getClass();
      
    
        
        try {
            // Itera sobre os métodos da classe
            for (Field field : clazz.getDeclaredFields()) {
                // Verifica se o método está anotado com @Id
                if (field.isAnnotationPresent(Id.class)) {
                    // Torna o método acessível caso ele seja privado
                	field.setAccessible(true);
                    // Executa o método e retorna o resultado
                	@SuppressWarnings("unchecked")
					T value = (T) field.get(obj);
                    return Optional.ofNullable(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retorna vazio caso não encontre um método anotado com @Id
        return Optional.empty();
    }
}