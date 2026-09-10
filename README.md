# VR Inventory Android V2

Proyecto Android nativo preparado para compilarse desde GitHub Actions, sin Android Studio ni Gradle instalado en el teléfono.

## Qué incluye
- Inicio de sesión demo.
- Bodegas.
- Productos.
- Inventario.
- Solicitudes de movimiento.
- Aprobar/rechazar movimientos.
- Actualización del inventario al aprobar.
- Workflow de GitHub Actions que genera automáticamente `app-debug.apk`.

## Cómo generar el APK desde el celular
1. Crea un repositorio en GitHub.
2. Sube todos los archivos y carpetas de este proyecto al repositorio (no hace falta instalar Gradle).
3. En GitHub entra en **Actions**.
4. Selecciona **Build APK**.
5. Pulsa **Run workflow**.
6. Cuando termine correctamente, abre la ejecución y busca **Artifacts**.
7. Descarga `InventoryAndroidV2-debug-apk.zip` y extrae `app-debug.apk`.
8. Instala el APK en tu Android.

La compilación se realiza en GitHub con Java 17 y Gradle 8.7.
