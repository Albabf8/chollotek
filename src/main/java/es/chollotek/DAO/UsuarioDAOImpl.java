package es.chollotek.DAO;

import es.chollotek.beans.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alba
 */
public class UsuarioDAOImpl implements UsuarioDAO{

    private static final Logger logger = Logger.getLogger(UsuarioDAOImpl.class.getName());

    @Override
    public Usuario buscarPorEmail(String email, Connection con) throws Exception {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.log(Level.INFO, "Usuario encontrado: {0}", email);
                    return mapear(rs);
                }
            }
        }
        
        logger.log(Level.INFO, "Usuario no encontrado: {0}", email);
        return null;
    }

    @Override
    public Usuario buscarPorId(int id, Connection con) throws Exception {
        String sql = "SELECT * FROM usuarios WHERE idusuario = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.log(Level.INFO, "Usuario encontrado por ID: {0}", id);
                    return mapear(rs);
                }
            }
        }
        
        logger.log(Level.WARNING, "Usuario no encontrado con ID: {0}", id);
        return null;
    }

    @Override
    public boolean emailExiste(String email, Connection con) throws Exception {
        String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE email = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean existe = rs.getInt("total") > 0;
                    logger.log(Level.INFO, "Verificaci\u00f3n email ''{0}'': {1}", new Object[]{email, existe ? "YA EXISTE" : "disponible"});
                    return existe;
                }
            }
        }
        
        return false;
    }

    @Override
    public void insertar(Usuario usuario, Connection con) throws Exception {
        String sql = "INSERT INTO usuarios " +
                     "(email, password, nombre, apellidos, nif, telefono, " +
                     "direccion, codigo_postal, localidad, provincia, avatar) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getEmail());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellidos());
            ps.setString(5, usuario.getNif());
            ps.setString(6, usuario.getTelefono());
            ps.setString(7, usuario.getDireccion());
            ps.setString(8, usuario.getCodigo_postal());
            ps.setString(9, usuario.getLocalidad());
            ps.setString(10, usuario.getProvincia());
            ps.setString(11, usuario.getAvatar());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar usuario, no se afectó ninguna fila.");
            }
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setIdusuario(keys.getInt(1));
                    logger.log(Level.INFO, "Usuario insertado correctamente: {0} (ID: {1})", new Object[]{usuario.getEmail(), usuario.getIdusuario()});
                }
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            logger.log(Level.SEVERE, "Email duplicado al intentar insertar: {0}", usuario.getEmail());
            throw new Exception("El email ya está registrado en el sistema.");
        }
    }

    @Override
    public void actualizar(Usuario usuario, Connection con) throws Exception {
        String sql = "UPDATE usuarios SET " +
                     "nombre = ?, " +
                     "apellidos = ?, " +
                     "telefono = ?, " +
                     "direccion = ?, " +
                     "codigo_postal = ?, " +
                     "localidad = ?, " +
                     "provincia = ?, " +
                     "avatar = ? " +
                     "WHERE idusuario = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getTelefono());
            ps.setString(4, usuario.getDireccion());
            ps.setString(5, usuario.getCodigo_postal());
            ps.setString(6, usuario.getLocalidad());
            ps.setString(7, usuario.getProvincia());
            ps.setString(8, usuario.getAvatar());
            ps.setInt(9, usuario.getIdusuario());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas == 0) {
                logger.log(Level.WARNING, "No se encontr\u00f3 usuario con ID: {0}", usuario.getIdusuario());
                throw new SQLException("No se pudo actualizar: usuario no encontrado.");
            }
            
            logger.log(Level.INFO, "Usuario actualizado: {0} - {1} {2}", new Object[]{usuario.getIdusuario(), usuario.getNombre(), usuario.getApellidos()});
        }
    }

    @Override
    public void actualizarPassword(int id, String passwordNueva, Connection con) throws Exception {
        String sql = "UPDATE usuarios SET password = ? WHERE idusuario = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, passwordNueva);
            ps.setInt(2, id);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas == 0) {
                logger.log(Level.WARNING, "No se encontr\u00f3 usuario con ID: {0}", id);
                throw new SQLException("No se pudo actualizar password: usuario no encontrado.");
            }
            
            logger.log(Level.INFO, "Contrase\u00f1a actualizada para usuario ID: {0}", id);
        }
    }

    @Override
    public void actualizarUltimoAcceso(int id, Connection con) throws Exception {
        String sql = "UPDATE usuarios SET ultimo_acceso = NOW() WHERE idusuario = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas == 0) {
                logger.log(Level.WARNING, "No se pudo actualizar \u00faltimo acceso para usuario ID: {0}", id);
            } else {
                logger.log(Level.FINE, "\u00daltimo acceso actualizado para usuario ID: {0}", id);
            }
        }
    }

    @Override
    public List<Usuario> listarTodos(Connection con) throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY apellidos, nombre";
        
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            
            logger.log(Level.INFO, "Usuarios listados: {0}", lista.size());
        }
        
        return lista;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        
        usuario.setIdusuario(rs.getInt("idusuario"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setNif(rs.getString("nif"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setDireccion(rs.getString("direccion"));
        usuario.setCodigo_postal(rs.getString("codigo_postal"));
        usuario.setLocalidad(rs.getString("localidad"));
        usuario.setProvincia(rs.getString("provincia"));
        usuario.setUltimo_acceso(rs.getTimestamp("ultimo_acceso"));
        usuario.setAvatar(rs.getString("avatar"));
        
        return usuario;
    }
    
}
