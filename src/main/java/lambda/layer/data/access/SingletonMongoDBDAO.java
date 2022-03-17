package lambda.layer.data.access;

public class SingletonMongoDBDAO {
	private static MongoDBDAO dao;

	public static MongoDBDAO getMongoDBDAO() {
		if (dao == null) {
			dao = new MongoDBDAOImpl();
		}
		return dao;
	}
}
