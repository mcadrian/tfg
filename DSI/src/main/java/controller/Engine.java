package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Rete;

public class Engine {

	private static final Engine unicaInstancia = new Engine();
	private File file;
	private final Rete rete;
	// selector de ficheros
	private final JFileChooser chooser;
	// extensiones aceptadas
	private final FileNameExtensionFilter filtroFicheros;


	/**
	 * Constructor del controlador
	 */

	public Engine() {
		rete = new Rete();
		// le asociamos las extensiones asociadas al selector de ficheros
		chooser = new JFileChooser();
		filtroFicheros = new FileNameExtensionFilter("ECG Files", "ecg");
		chooser.setFileFilter(filtroFicheros);
	}



	/**
	 * Metodo singleton que devuelve el controlador del sistema
	 *
	 * @return El controlador
	 */

	public static Engine getUnicaInstancia() {
		return unicaInstancia;
	}



	/**
	 * Metodo que permite al usuario seleccionar un fichero y lo abre para
	 * devolver un bufferedReader Si el fichero no existe lanza una excepcion
	 *
	 * @return El bufferedReader del fichero que se abre
	 */

	public BufferedReader openFile() {
		BufferedReader bufferedReader = null;
		int result = chooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			// para que se quede con la ultima ruta si queremos cargar otro fichero
			chooser.setCurrentDirectory(file.getParentFile());
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(file);
				bufferedReader = new BufferedReader(fileReader);
			} catch (FileNotFoundException ex) {
				System.out.println("Fichero no encontrado.");
			}
		}
		return bufferedReader;
	}


	/**
	 * Metodo que con el fichero abierto, crea las correspondientes ondas,
	 * intervalos, etc. Ademas infiere las enfermedades, añadiendolas a una
	 * estructura de datos para luego poder mostrarlas.
	 *
	 * @param idioma Idioma en el que devuelve el menaje de error. 0 ingles, 1
	 * español
	 * @return Devuelve una cadena vacia si no ha habido un error o el tipo de
	 * error en caso de haberlo al leer el fichero
	 */
	public String loadFile(int idioma) {

		String mensajeError = "";
		// Reseteamos
		rete.resetear();
		String ecgString = "";
		BufferedReader bufferedReader = openFile();
		boolean ciclosOrdenados = true;
		boolean repetida = false;
		// tenemos el wave que representa una onda con sus tres atributos
		String[] wave;
		// la lista auxiliar donde tendremos las ondas antes de introducirlos como hechos, sirve para comprobar bien el ciclo
		LinkedList<String[]> listaWaves = new LinkedList<String[]>();

		if (bufferedReader != null) {
			try {
				// Lectura del fichero
				String linea;
				// Lee la cabecera de los ficheros de validation...
				while (((linea = bufferedReader.readLine()) != null) && !(linea.startsWith("P(")) && !(linea.startsWith("Q(")) && !(linea.startsWith("R(")) && !(linea.startsWith("S(")) && !(linea.startsWith("T("))) {
					//nothing
				}
				// Lee las ondas del ECG
				do {
					ecgString += linea + " ";
				} while ((linea = bufferedReader.readLine()) != null);
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}

			// En el array ondasAux tenemos cada linea del fichero de entrada por separado
			String[] ondasAux = ecgString.split(" ");
			String tipoOnda;
			int start, end;
			float peak;
			try {
				for (int i = 0; i < ondasAux.length && ciclosOrdenados && !repetida; i++) {
					String s = ondasAux[i];
					tipoOnda = ("" + s.charAt(0)).toUpperCase();
					//Nos quedamos con los valores de la onda:
					String[] valores = (s.substring(2, s.length() - 1)).split(",");
					start = Integer.parseInt(valores[0]);
					end = Integer.parseInt(valores[1]);
					peak = Float.parseFloat(valores[2]);

					// comprobacion para la primera onda
					if (i == 0) {
						if (start > end) {
							if (idioma == 0) {
								mensajeError = "ECG Error: Incorrect wave:\n\t" + tipoOnda + " wave starts before it ends in cycle " + rete.getNumCiclo() + ".\n";
							} else {
								mensajeError = "Error en el ECG: Onda incorrecta:\n\t En el ciclo " + rete.getNumCiclo() + " una onda " + tipoOnda + " tiene un inicio posterior a su final\n";
							}
							ciclosOrdenados = false;
						}
					} //comprobacion para ondas restantes
					else {
						// En cuanto estes en la segunda onda...
						// ... se comprueba que las ondas tengan valores correctos
						if (start > end) {
							if (idioma == 0) {
								mensajeError = "ECG Error: Incorrect wave:\n\t" + tipoOnda + " empieza antes de que acabe en el ciclo " + rete.getNumCiclo() + ".\n";
							} else {
								mensajeError = "Error en el ECG: Onda incorrecta:\n\t En el ciclo " + rete.getNumCiclo() + " una onda " + tipoOnda + " tiene un inicio posterior a su final\n";
							}
							ciclosOrdenados = false;
						}
						// cogemos los datos de la onda anterior, en concreto el dato del valor 1 que es el valor end
						int endAnterior = Integer.parseInt((ondasAux[i - 1].substring(2, ondasAux[i - 1].length() - 1)).split(",")[1]);
						String tipoOndaAnterior = ("" + ondasAux[i - 1].charAt(0)).toUpperCase();
						if (start < endAnterior) {
							// si el inicio de la siguiente onda es anterior al final de la anterior entonces error
							if (idioma == 0) {
								mensajeError += "ECG Error: Incorrect wave:\n\t" + tipoOnda + " wave starts before " + tipoOndaAnterior + " wave ends in cycle " + rete.getNumCiclo();
							} else {
								mensajeError += "Error en el ECG: Onda incorrecta:\n\t Una onda " + tipoOnda + " empieza antes de que su onda anterior " + tipoOndaAnterior + " acabe en el ciclo " + rete.getNumCiclo();

							}
							ciclosOrdenados = false;
						}

					}
					if (!tipoOnda.equals("R")) {
						for (String[] onda : listaWaves) {
							if (onda[3].equals(tipoOnda)) {
								repetida = true;
							}
						}
						if (repetida) {
							if (idioma == 0) {
								mensajeError += "ECG Error: Incorrect wave:\n\t" + tipoOnda + " appears more than once in the cycle " + rete.getNumCiclo() + " or a R wave is missing.";
							} else {
								mensajeError += "Error en el ECG: Onda incorrecta:\n\t  En el ciclo " + rete.getNumCiclo() + " debido a que una onda " + tipoOnda + " aparece mas de una vez o que falta una onda R";
							}
						} else {
							wave = new String[4];
							wave[0] = String.valueOf(start);
							wave[1] = String.valueOf(end);
							wave[2] = String.valueOf(peak);
							wave[3] = String.valueOf(tipoOnda);
							listaWaves.add(wave);
						}

					} else {
						rete.avanzarCiclo();
						for (String[] onda : listaWaves) {
							if (onda[3].equals("S") || onda[3].equals("T")) {
								addWave(Integer.parseInt(onda[0]), Integer.parseInt(onda[1]), Float.parseFloat(onda[2]), onda[3], rete.getNumCiclo() - 1);
							} else if (onda[3].equals("P") || onda[3].equals("Q")) {
								addWave(Integer.parseInt(onda[0]), Integer.parseInt(onda[1]), Float.parseFloat(onda[2]), onda[3], rete.getNumCiclo());
							}
						}
						addWave(start, end, peak, tipoOnda, rete.getNumCiclo());
						listaWaves.clear();
					}
				}
			} catch (NumberFormatException excepcionFichero) {
				// borramos si hubiera algo ya en la base de hechos y mostramos mensaje
				rete.resetear();
				if (idioma == 0) {
					mensajeError = "File Error: File format error.\n";
				} else {
					mensajeError = "Error en el fichero: Formato incorrecto.\n";
				}
			}
		} else {
			mensajeError = "noFile";
		}
		// antes de terminar, comprobamos si se ha producido algun error y en ese caso borramos la base de hechos
		if (!ciclosOrdenados || repetida) {
			rete.resetear();
		}
		return mensajeError;
	}


	/**
	 * Metodo que añade una onda en nuestra base de hechos
	 *
	 * @param start Inicio de la onda
	 * @param end Final de la onda
	 * @param peak Pico de la onda
	 * @param type Tipo de onda
	 * @param numCiclo Numero de ciclo de la onda
	 *
	 */
	public void addWave(int start, int end, float peak, String type, int numCiclo) {
		rete.addWave(type, start, end, peak, numCiclo);
	}

	/**
	 * Metodo para ejecutar el Rete
	 *
	 */
	public void run() {
		rete.run();
	}

	/**
	 * Metodo que muestra las reglas de nuestro sistema
	 *
	 * @param areaTexto Area de texto donde muestra las reglas
	 */
	public void showRules(JTextArea areaTexto) {
		String reglas = rete.getRules();
		areaTexto.setText(reglas);
	}

	/**
	 * Metodo que muestra los hechos de nuestro sistema
	 *
	 * @param areaTexto Area de texto donde muestra los hechos
	 */
	public void showFacts(JTextArea areaTexto) {
		String hechos = rete.getFacts();
		areaTexto.setText(hechos);
	}

	/**
	 * Metodo que muestra la agenda de nuestro sistema
	 *
	 * @param areaTexto Area de texto donde muestra las reglas que pueden
	 * dispararse
	 * @param idioma Idioma en el que se muestra el texto. 0 ingles, 1 español
	 */
	public void showAgenda(JTextArea areaTexto, int idioma) {
		String agenda = rete.getAgenda();
		if (agenda.equals("")) {
			if (idioma == 0) {
				agenda = "There are no rules to fire because the engine was already ran.";
			} else {
				agenda = "No hay reglas para disparar porque el sistema ya fue ejecutado.";
			}
		}
		areaTexto.setText(agenda);
	}

	/**
	 * Metodo que rellena la casilla de numero de ciclos una vez que ejecutamos
	 *
	 * @param textField La casilla correspondiente al numero de ciclos
	 */
	public void rellenarCiclos(JTextField textField) {
		textField.setText(String.valueOf(rete.getNumCiclo()));
	}

	/**
	 * Metodo que rellena la casilla del BPM final una vez que ejecutamos
	 *
	 * @param textField La casilla correspondiente al BPM
	 */
	public void rellenarBPM(JTextField textField) {
		textField.setText(String.valueOf(rete.getBpm()));
	}

	/**
	 * Metodo que devuelve el valor del diagnostico tras la inferencia de reglas
	 *
	 * @param idioma Idioma en el que se muestra el diagnostico, 0 ingles 1
	 * español
	 * @return un string con el diagnostico o un mensaje de error si se ha
	 * producido alguno leyendo de fichero.
	 */
	public String getDiagnostico(int idioma) {
		return rete.getDiagnostico(idioma);
	}

	/**
	 * Metodo que devuelve los valores medios de cada segmento, intervalo y
	 * complejo QRS en forma de string
	 *
	 * @param idioma Idioma en el que se muestran los valores medios. 0 ingles 1
	 * español
	 * @return un string con los valores medios.
	 */
	private String getValoresMedios(int idioma) {
		return rete.getValoresMedios(idioma);
	}

	/**
	 * Metodo que rellena el area del texto con la informacion del diagnostico y
	 * los valores medios de los segmentos intervalos y complejo QRS.
	 *
	 * @param textoInfo El area donde se rellena el diagnostico
	 * @param idioma Idioma en el que se muestra el diagnostico. 0 Ingles, 1
	 * Español
	 */
	public void rellenarDiagnostico(JTextArea textoInfo, int idioma) {
		String diagnostico = getDiagnostico(idioma);
		diagnostico += getValoresMedios(idioma);
		textoInfo.setText(diagnostico);
	}

}

