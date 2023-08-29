package sp.TESTE;

import java.io.*;
import java.util.*;

/**
 * Hello world!
 *
 */
public class Main {
	private static final String OUTPUT_FILE = "output.txt";

	public static void main(String[] args) {
		try {
			InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("Frames.txt");
			Scanner scanner = new Scanner(inputStream);
			List<Integer> values = new ArrayList<>();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] hexValues = line.trim().split(" ");

				for (String hexValue : hexValues) {
					try {
						int intValue = Integer.parseInt(hexValue, 16);
						values.add(intValue);
					} catch (Exception e) {
						System.out.println("Invalid hexadecimal value: " + hexValue);
					}

				}

			}
			processaFrame(values);
			System.out.println("acabou");

			scanner.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void processaFrame(List<Integer> values) {
		try (PrintWriter outputWriter = new PrintWriter(new FileWriter(OUTPUT_FILE))) {

			for (int i = 0; i < values.size(); i++) {
				int perguntaSize = 0;
				int respostaSize = 0;

				perguntaSize = values.get(i) - 0x80 + 4;
				respostaSize = 0;
				if (perguntaSize > 4) {
					if (i + perguntaSize >= values.size()) {
						System.out.println("Insufficient data for question at index " + i);
						continue;
					}

					if (i + perguntaSize + respostaSize >= values.size()) {
						System.out.println("Insufficient data for answer at index " + i);
						System.out.println(values.size());
						continue;

					} else {
						respostaSize = values.get(i + perguntaSize) - 0x80 + 4;
					}

				}

				if (perguntaSize > 4 && respostaSize > 4) {
					// pergunta
					int[] pergunta = new int[perguntaSize];
					for (int p = 0; p < perguntaSize; p++) {
						pergunta[p] = values.get(i + p);
					}
					// resposta
					int[] resposta = new int[respostaSize];
					for (int r = 0; r < respostaSize; r++) {
						if ((i + perguntaSize + r) >= values.size()) {
							continue;
						} else {
							resposta[r] = values.get(i + perguntaSize + r);
						}
					}

					if (pergunta[1] == resposta[2] && pergunta[2] == resposta[1]) {
						if (pergunta[3] + 64 == (resposta[3]) || resposta[3] == 127) {
							String[] perguntaStrings = new String[pergunta.length];
							String[] respostaStrings = new String[resposta.length];

							for (int p = 0; p < pergunta.length; p++) {
								if (pergunta[p] < 16) {
									perguntaStrings[p] = "0" + Integer.toHexString(pergunta[p]);
								} else {
									perguntaStrings[p] = Integer.toHexString(pergunta[p]);
								}
							}
							for (int r = 0; r < resposta.length; r++) {
								if (resposta[r] < 16) {
									respostaStrings[r] = "0" + Integer.toHexString(resposta[r]);
								} else {
									respostaStrings[r] = Integer.toHexString(resposta[r]);
								}

							}
							// tirando as [] e , dos arrays e adicionando espaços (formatando)
							String perguntaString = formatArray(perguntaStrings);
							String respostaString = formatArray(respostaStrings);

							// escreve a pergunta e resposta
							if (i > 1 && values.get(i - 1) == 0) {
								outputWriter.println("00 " + perguntaString.toUpperCase());
							} else {
								outputWriter.println(perguntaString.toUpperCase());
							}

							outputWriter.println(respostaString.toUpperCase());
							outputWriter.println();

						}

					}
				}

			}
			System.out.println("acabou");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String formatArray(String[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				sb.append(' ');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}
  }
