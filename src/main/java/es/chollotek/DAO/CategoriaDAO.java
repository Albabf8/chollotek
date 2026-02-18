package es.chollotek.DAO;

import es.chollotek.beans.Categoria;
import java.util.List;

/**
 *
 * @author Alba
 */
public interface CategoriaDAO {

    public List<Categoria> getCategorias();
    
    public void closeConnection();
}
