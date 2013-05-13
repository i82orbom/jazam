/**
 * 
 */
package util.collections.songs;

/**
 * @author Tabata
 *
 */
public enum Song {

	/** weight expressed in MB **/
	SONG1(1, "", 5.0),
	SONG2(1, "", 5.0);
	
	Song(int id, String name, double weight){
		this.id = id;
		this.name = name;
		this.weight = weight;
	}
	
	private int id;
	private String name;
	private double weight;
}
