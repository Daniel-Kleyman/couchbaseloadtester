package kleyman.service;

/**
 * Interface for database services.
 */
public interface DataBaseService<T> {

    /**
     * Inserts an object into the database by key.
     *
     * @param key  the key under which to store the object
     * @param data the object to insert
     */
    void upload(String key, T data);

    /**
     * Retrieves an object from the database by key.
     *
     * @param key the key of the object to retrieve
     * @return the retrieved object
     */
    T retrieve(String key);
}
