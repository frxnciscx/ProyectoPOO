# Sistema de Gestion de Medicamentos
Proyecto final desarrollado para la asignatura de **Programación Orientada a Objetos** de la
Universidad de La Frontera.

## Descripcion 
Aplicación de escritorio diseñada para apoyar la adherencia terapéutica en pacientes con enfermedades crónicas y
cuidadores. El sistema permite administrar esquemas de polimedicación, controlar el inventario de fármacos, y 
programar alertas de dosis, incorporando lógica de seguridad específica para tratamientos con insulina.

## Caracteristicas principales
* **Gestión de Pacientes**: Registro validado y autenticación simplificada mediante RUT (sin barreras de contraseña)
* **Inventario Inteligente:** Control de stock y alertas preventivas de fechas de vencimiento.
* **Soporte para Insulina:** Lógica polimórfica que valida la administración según niveles de glucosa del paciente.
* **Sistema de Recordatorios:** Monitor en segundo plano (`Timer`) que despliega alertas visuales para la toma de medicamentos.
* **Persistencia Robusta:** Almacenamiento estructurado en formato JSON, gestionado mediante la librería **Google Gson**.
* **Resiliencia y Seguridad:** Sistema de "blindaje" con generación automática de copias de seguridad (`_backup.json`) ante cada operación de escritura.

## Arquitectura
El proyecto sigue el patrón de arquitectura **Modelo-Vista-Controlador (MVC)** para garantizar la separación de responsabilidades.
* **Gestión de Dependencias:** Maven
* **Interfaz Gráfica:** Java Swing con FlatLaf.
* **Persistencia:** Google Gson 2.10.1
* **Testing:** JUnit 5 (Pruebas unitarias para reglas de negocio).

### Estructura de Paquetes
* `proyecto.modelo`: Entidades del dominio (`Paciente`, `Medicamento`, `Insulina`, `Recordatorio`).
* `proyecto.modelo.datos`: Clases encargadas de la persistencia y repositorios (`GestorDatosPaciente`).
* `proyecto.vista`: Interfaces de usuario y lógica de presentación.
* `proyecto.controlador`: Coordinadores de flujo y lógica de aplicación.

## Diagrama de Clases
![ProyectoPOO.jpg](src/main/java/proyecto/ProyectoPOO.jpg)

## Autores
* Francisca Aguayo Benzan
* Natalia Pérez González
