import java.io.IOException;


public class Generator {
	public static void main(String[] args) throws IOException {
		 double noMax=0.2;
		 double noMin = 0.02;
		 int n = 16;
		 int m = 2;
		 double yes = 0.02+0.05;
		 double random;
		 int Matrix[][] = new int[16][16];
		 //for(noMin=0.02; noMin<= noMax; noMin = noMin +0.02)
		 //{
			 for(int i = 0;i<n; i++)
			 {
				 for(int j = 0; j<n; j++)
				 {
					 random = Math.random();
					 if(random <= noMin)
						 Matrix[i][j] = 1;
					 else if((noMin < random) && (random < yes))
						 Matrix[i][j] = -1;	
					 else
						 Matrix[i][j] = 0;
				 }
			 }			 			 
		 //}
		for(int i = 0; i < n; i++)
		{
			for(int j= 0; j < n; j++)
			{
				System.out.print(Matrix[i][j]+" ");
			}
			System.out.println("\n");
		}
		
	}
}
