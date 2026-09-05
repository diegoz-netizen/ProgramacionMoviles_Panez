# Registro de Productos - Lab 03

**Estudiante:** Diego Panez Rondinel
**Curso:** Desarrollo de Aplicaciones Móviles

Aplicación móvil desarrollada en Kotlin con Jetpack Compose para el registro y cálculo del importe total de productos.

---

## Mejora con IA

En esta sección se detallan las iteraciones realizadas con Gemini para mejorar la aplicación, diferenciando el código generado automáticamente de las correcciones manuales aplicadas para cumplir con los casos de prueba requeridos.

| Prompt que usé | Qué generó Gemini | Qué acepté o corregí (y por qué) |
| :--- | :--- | :--- |
| **Prompt 1 (Añadir validaciones y botón limpiar):**<br>"Ayúdame a agregar validaciones a los campos vacíos en el formulario de registro y añade un botón Limpiar para borrar los datos ingresados." | Generó la estructura del botón **LIMPIAR**, la validación para verificar campos vacíos y la lógica para mostrar una alerta inicial si faltaba información. | **Aceptado parcial:** Se integró la estructura básica del botón y el estado de error, pero se identificó que la validación generada era muy permisiva con datos numéricos. |
| **Prompt 2 (Refinamiento y casos bordes):**<br>"Agrega validaciones adicionales: el nombre no debe aceptar caracteres especiales, el precio y la cantidad deben ser exclusivamente números y no se deben permitir valores negativos o cero." | Código ajustado con expresiones regulares (`Regex`) y restricciones numéricas para limitar valores menores o iguales a 0. | **Aceptado y corregido (Commit B2):** Se adaptaron e integraron estas validaciones en el bloque `when` para asegurar que el usuario reciba mensajes de error claros y específicos ante cualquier entrada inválida (como cantidades o precios negativos). |

