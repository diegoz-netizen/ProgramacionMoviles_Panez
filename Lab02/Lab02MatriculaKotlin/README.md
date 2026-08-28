# Lab02MatriculaKotlin - Sistema de Registro de Matrícula

Este proyecto es una aplicación de consola en **Kotlin** desarrollada de manera procedural para la gestión del proceso de matrícula académica.

## Funcionalidades

- **Entrada de datos con validaciones:** Captura interactiva del estudiante, cantidad de cursos, costo por crédito y datos de cada materia (nombre y créditos).
- **Cálculo automático de costos:** Determinación del total de créditos y monto total a pagar.
- **Evaluación de reglas de negocio:**
    - **Carga Académica:**
        - `≤ 12 créditos`: Matrícula regular
        - `13 a 18 créditos`: Carga completa
        - `> 18 créditos`: Requiere autorización
    - **Forma de Pago:**
        - `> S/ 2500.00`: 3 cuotas
        - `≤ S/ 2500.00`: 2 cuotas
- **Reporte formateado:** Salida limpia y alineada en consola con resumen detallado por asignatura.

## Commits del proyecto
1. `Commit 1: Lectura e ingreso de datos por consola`
2. `Commit 2: Logica de negocio, calculos y validaciones de entrada`
3. `Commit 3: Reporte final formateado por consola y README`