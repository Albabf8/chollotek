// ══════════════════════════════════════════════════════════════
// VALIDACIONES EN TIEMPO REAL PARA FORMULARIO DE REGISTRO
// ══════════════════════════════════════════════════════════════

document.addEventListener('DOMContentLoaded', function() {
    
    // ─────────────────────────────────────────────────────────
    // Validar email con Ajax (verifica si ya existe en BD)
    // ─────────────────────────────────────────────────────────
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            validarEmailAjax(this.value);
        });
    }

    // ─────────────────────────────────────────────────────────
    // Validar que las contraseñas coincidan
    // ─────────────────────────────────────────────────────────
    const password2Input = document.getElementById('password2');
    if (password2Input) {
        password2Input.addEventListener('blur', function() {
            validarPasswordsIguales();
        });
    }

    // ─────────────────────────────────────────────────────────
    // Calcular letra del NIF con Ajax
    // ─────────────────────────────────────────────────────────
    const nifInput = document.getElementById('nif');
    if (nifInput) {
        nifInput.addEventListener('input', function() {
            const valor = this.value.replace(/[^0-9]/g, ''); // Solo números
            this.value = valor;
            
            if (valor.length === 8) {
                calcularLetraNIFAjax(valor);
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // Validación del formulario completo antes de enviar
    // ─────────────────────────────────────────────────────────
    const formRegistro = document.getElementById('formRegistro');
    if (formRegistro) {
        formRegistro.addEventListener('submit', function(e) {
            if (!validarFormulario()) {
                e.preventDefault();
                alert('Por favor, corrige los errores en el formulario.');
            }
        });
    }
});

/**
 * Valida que el email no exista en la BD mediante Ajax.
 * Llama al AjaxController para verificar disponibilidad.
 * 
 * @param {string} email - Email a validar
 */
function validarEmailAjax(email) {
    if (!email || email.trim() === '') return;

    const errorSpan = document.getElementById('emailError');
    errorSpan.textContent = 'Verificando...';
    errorSpan.style.color = 'gray';

    // Petición Ajax al servidor
    fetch('AjaxController?accion=emailExiste&email=' + encodeURIComponent(email))
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                errorSpan.textContent = '⚠ ' + data.error;
                errorSpan.style.color = 'orange';
            } else if (data.existe) {
                errorSpan.textContent = '✗ Este email ya está registrado';
                errorSpan.style.color = 'red';
            } else {
                errorSpan.textContent = '✓ Email disponible';
                errorSpan.style.color = 'green';
            }
        })
        .catch(error => {
            console.error('Error en Ajax:', error);
            errorSpan.textContent = '⚠ Error al verificar email';
            errorSpan.style.color = 'orange';
        });
}

/**
 * Valida que las dos contraseñas introducidas coincidan.
 * Validación del lado del cliente antes de enviar el formulario.
 * 
 * @return {boolean} true si coinciden, false si no
 */
function validarPasswordsIguales() {
    const pass1 = document.getElementById('password').value;
    const pass2 = document.getElementById('password2').value;
    const errorSpan = document.getElementById('passwordError');

    if (!pass2) {
        errorSpan.textContent = '';
        return true;
    }

    if (pass1 !== pass2) {
        errorSpan.textContent = '✗ Las contraseñas no coinciden';
        errorSpan.style.color = 'red';
        return false;
    } else {
        errorSpan.textContent = '✓ Las contraseñas coinciden';
        errorSpan.style.color = 'green';
        return true;
    }
}

/**
 * Calcula la letra del NIF mediante Ajax.
 * Usa el algoritmo oficial del DNI español.
 * 
 * @param {string} numeros - 8 dígitos del NIF
 */
function calcularLetraNIFAjax(numeros) {
    if (numeros.length !== 8 || !/^[0-9]{8}$/.test(numeros)) {
        return;
    }

    const letraNIF = document.getElementById('nifLetra');
    letraNIF.textContent = 'Calculando...';
    letraNIF.style.color = 'gray';

    // Petición Ajax al servidor
    fetch('AjaxController?accion=calcularNIF&numeros=' + numeros)
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                letraNIF.textContent = '⚠ ' + data.error;
                letraNIF.style.color = 'orange';
            } else {
                letraNIF.textContent = '✓ Letra: ' + data.letra;
                letraNIF.style.color = 'green';
                
                // Actualizar el valor del input con el NIF completo
                document.getElementById('nif').value = data.nifCompleto;
            }
        })
        .catch(error => {
            console.error('Error en Ajax:', error);
            letraNIF.textContent = '⚠ Error al calcular letra';
            letraNIF.style.color = 'orange';
        });
}

/**
 * Validación completa del formulario antes de enviar.
 * Verifica todos los campos críticos.
 * 
 * @return {boolean} true si el formulario es válido, false si hay errores
 */
function validarFormulario() {
    let valido = true;

    // Validar contraseñas coincidentes
    if (!validarPasswordsIguales()) {
        valido = false;
    }

    // Validar código postal (5 dígitos)
    const cp = document.getElementById('codigo_postal');
    if (cp && !/^[0-9]{5}$/.test(cp.value)) {
        alert('El código postal debe tener 5 dígitos');
        valido = false;
    }

    // Validar teléfono si está relleno (9 dígitos)
    const telefono = document.getElementById('telefono');
    if (telefono && telefono.value && !/^[0-9]{9}$/.test(telefono.value)) {
        alert('El teléfono debe tener 9 dígitos');
        valido = false;
    }

    // Validar que el NIF esté completo (8 números + 1 letra)
    const nif = document.getElementById('nif');
    if (nif && !/^[0-9]{8}[A-Z]$/.test(nif.value)) {
        alert('El NIF debe tener 8 números seguidos de una letra');
        valido = false;
    }

    return valido;
}

// ══════════════════════════════════════════════════════════════
// FUNCIONES AJAX PARA EL CARRITO (AUMENTAR/DISMINUIR CANTIDAD)
// ══════════════════════════════════════════════════════════════

/**
 * Aumenta la cantidad de un producto en el carrito sin recargar la página.
 * 
 * @param {number} idLinea - ID de la línea del pedido
 * @param {number} cantidadActual - Cantidad actual del producto
 */
function sumarCantidadAjax(idLinea, cantidadActual) {
    const url = `AjaxController?accion=sumarCantidad&idlinea=${idLinea}&cantidadActual=${cantidadActual}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data.exito) {
                // Actualizar la cantidad en la página sin recargar
                actualizarCantidadEnPagina(idLinea, data.nuevaCantidad);
                mostrarMensaje('Cantidad actualizada', 'success');
            } else if (data.error) {
                mostrarMensaje('Error: ' + data.error, 'error');
            }
        })
        .catch(error => {
            console.error('Error Ajax:', error);
            mostrarMensaje('Error de conexión', 'error');
        });
}

/**
 * Disminuye la cantidad de un producto en el carrito sin recargar la página.
 * Si la cantidad llega a 0, elimina el producto del carrito.
 * 
 * @param {number} idLinea - ID de la línea del pedido
 * @param {number} cantidadActual - Cantidad actual del producto
 */
function restarCantidadAjax(idLinea, cantidadActual) {
    const url = `AjaxController?accion=restarCantidad&idlinea=${idLinea}&cantidadActual=${cantidadActual}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data.exito) {
                if (data.eliminado) {
                    // Eliminar la fila de la tabla
                    eliminarFilaCarrito(idLinea);
                    mostrarMensaje('Producto eliminado del carrito', 'info');
                } else {
                    // Actualizar la cantidad en la página
                    actualizarCantidadEnPagina(idLinea, data.nuevaCantidad);
                    mostrarMensaje('Cantidad actualizada', 'success');
                }
            } else if (data.error) {
                mostrarMensaje('Error: ' + data.error, 'error');
            }
        })
        .catch(error => {
            console.error('Error Ajax:', error);
            mostrarMensaje('Error de conexión', 'error');
        });
}

/**
 * Actualiza la cantidad mostrada en la página del carrito.
 * 
 * @param {number} idLinea - ID de la línea a actualizar
 * @param {number} nuevaCantidad - Nueva cantidad a mostrar
 */
function actualizarCantidadEnPagina(idLinea, nuevaCantidad) {
    const cantidadSpan = document.querySelector(`[data-linea="${idLinea}"] .cantidad`);
    if (cantidadSpan) {
        cantidadSpan.textContent = nuevaCantidad;
    }
}

/**
 * Elimina una fila del carrito de la página.
 * 
 * @param {number} idLinea - ID de la línea a eliminar
 */
function eliminarFilaCarrito(idLinea) {
    const fila = document.querySelector(`[data-linea="${idLinea}"]`);
    if (fila) {
        fila.remove();
    }

    // Si no quedan filas, mostrar mensaje de carrito vacío
    const tbody = document.querySelector('.tabla-carrito tbody');
    if (tbody && tbody.children.length === 0) {
        location.reload(); // Recargar para mostrar el mensaje de carrito vacío
    }
}

/**
 * Muestra un mensaje temporal en la página.
 * 
 * @param {string} mensaje - Texto del mensaje
 * @param {string} tipo - Tipo: 'success', 'error', 'info'
 */
function mostrarMensaje(mensaje, tipo) {
    // Crear elemento de alerta
    const alerta = document.createElement('div');
    alerta.className = `alert alert-${tipo}`;
    alerta.textContent = mensaje;

    // Insertar al inicio del contenedor
    const container = document.querySelector('.container');
    if (container) {
        container.insertBefore(alerta, container.firstChild);
    }

    // Eliminar después de 3 segundos
    setTimeout(() => {
        alerta.remove();
    }, 3000);
}