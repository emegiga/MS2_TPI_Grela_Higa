# ADR-002 — Repositorios por interfaz, persistencia en memoria e inyección por constructor

**Estado:** aceptada

## Contexto
`GestorSolicitudes` guardaba listas públicas y cargaba los datos de ejemplo en su constructor (CS-02,
CS-12). Cambiar la forma de persistir obligaba a reescribirlo, y no se podía crear un gestor vacío
para probarlo.

## Decisión
- Tres interfaces chicas (`SolicitudRepositorio`, `EstudianteRepositorio`, `ResponsableRepositorio`),
  una por necesidad del gestor (ISP), con implementaciones en memoria.
- El gestor recibe los repositorios y un `Clock` por constructor (DIP). `Main` es el único que conoce
  las clases concretas.
- Se **mantiene la persistencia en memoria** del sistema base.

## Alternativas descartadas
- **Persistir en archivo o base de datos:** agregaba funcionalidad y cambiaba el comportamiento
  (los datos sobrevivirían al cierre), fuera del objetivo del TPI. La interfaz deja el camino preparado.
- **Un único repositorio genérico:** más simple de declarar, pero obligaba a implementar operaciones
  que estudiantes y responsables no necesitan (solo se consultan).

## Consecuencias
- ✅ Las pruebas del gestor usan repositorios propios y una fecha fija.
- ✅ Pasar a otra persistencia es agregar una implementación sin tocar el gestor (OCP).
- ⚠️ Más archivos (6 en el paquete `repositorio`) para un sistema chico.
