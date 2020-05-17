package original.jviolajones;


public class Rect {

	int x1,x2,y1,y2;
	float weight;
	public Rect(int x1,int x2,int y1,int y2,float weight) {
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
		this.weight=weight;
	}
	public static Rect fromString(String text) {
		String[] tab=text.split(" ");
		int x1=Integer.parseInt(tab[0]);
		int x2=Integer.parseInt(tab[1]);
		int y1=Integer.parseInt(tab[2]);
		int y2=Integer.parseInt(tab[3]);
		float f=Float.parseFloat(tab[4]);
		return new Rect(x1,x2,y1,y2,f);
	}

}
