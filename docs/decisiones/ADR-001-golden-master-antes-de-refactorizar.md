# ADR-001 — Pruebas de caracterización (golden master) antes de refactorizar

**Estado:** aceptada

## Contexto
El sistema base no tenía pruebas. La lógica imprimía directamente por consola, el gestor cargaba sus
propios datos y tomaba la fecha del sistema: no se podían escribir pruebas unitarias sin modificar el
código primero, y modificarlo sin pruebas no es un refactoring seguro.

## Decisión
Probar el sistema **desde afuera**: ejecutar `Main.main()` con entradas simuladas, capturar toda la
salida y compararla con archivos aprobados generados con `estado-inicial` (19 escenarios). Esta suite
se commiteó antes de tocar cualquier línea de producción.

## Alternativas descartadas
- **Pruebas manuales antes/después:** lentas de repetir después de cada uno de los 10 refactorings y
  propensas a omisiones.
- **Pruebas unitarias desde el principio:** requerían cambiar el diseño antes de tener red de seguridad.

## Consecuencias
- ✅ Cada refactoring se verificó en segundos y la suite atrapó una regresión real (E19).
- ✅ Las mismas pruebas, corridas sobre `estado-inicial`, demuestran la equivalencia antes/después.
- ⚠️ Son frágiles ante cambios de redacción: cualquier cambio en un mensaje rompe un golden. Es
  aceptable porque el objetivo era justamente no cambiar nada visible.
