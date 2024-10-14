package kleyman.service;

/**
 * Interface for database services.
 *
 * @param <T> the type of the object to be stored and retrieved
 * @param <M> the type of the metrics used for tracking performance
 */
public interface DataBaseService<T, M> {

    void upload(String key, T data, M metrics);

    T retrieve(String key, M metrics);
}
