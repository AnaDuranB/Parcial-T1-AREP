package arep.parcial;

import java.util.Arrays;
// clase prueba bubblesort
public class bubble {
    public static void main(String[] args) {
        int[] lista = {5,3,8,4,2};
        bubbleSort(lista);
    }
    private static int[] bubbleSort(int[] lista){
        for (int i = lista.length; i>0; i++){
            for (int j = 0; j < lista.length -1; j++){
                if (lista[j] > lista[j+1]) {
                    int actual = lista[j];
                    lista[j] = lista[j+1];
                    lista[j+1] = actual;
                }
            }
        }
        System.out.println(Arrays.toString(lista));
        return lista;
    }
}
