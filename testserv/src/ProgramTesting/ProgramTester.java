package ProgramTesting;

import ProgramTesting.Exceptions.UnsuccessException;
import ProgramTesting.Exceptions.RunTimeErrorException;
import Program.Program;
import DataProcessing.Exceptions.ComparisonFailedException;
import DataProcessing.Exceptions.InputWriteException;
import DataProcessing.Exceptions.OutputReadException;
import DataProcessing.InputDataProcessor;
import DataProcessing.OutputDataProcessor;
import FileOperations.FileOperator;
import IOGenerating.InputGenerator;
import IOGenerating.OutputGenerator;
import ProcessExecuting.Exceptions.ProcessExecutingException;
import ProcessExecuting.ProcessExecutor;
import ProgramTesting.Exceptions.TestingInternalServerErrorException;
import ProgramTesting.Exceptions.TestingTimeLimitExceededException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Класс для тестирования программ.
 *
 * @author partizanka
 */
public class ProgramTester {

    private InputGenerator inputGenerator;
    private OutputGenerator outputGenerator;
    private InputDataProcessor inputDataProcessor;
    private OutputDataProcessor outputDataProcessor;
    private StringBuffer message;

    /**
     * Конструктор класса для тестирования программ.
     *
     * @param inputGenerator генератор входных данных
     * @param outputGenerator генератор выходных данных
     * @param inputDataProcessor обработчик входных данных
     * @param outputDataProcessor обработчик выходных данных
     */
    public ProgramTester(InputGenerator inputGenerator,
            OutputGenerator outputGenerator,
            InputDataProcessor inputDataProcessor,
            OutputDataProcessor outputDataProcessor) {
        this.inputGenerator = inputGenerator;
        this.outputGenerator = outputGenerator;
        this.inputDataProcessor = inputDataProcessor;
        this.outputDataProcessor = outputDataProcessor;
    }

    /**
     * Выполняет и тестирует программу на всех тестах до первого проваленного.
     *
     * TODO memory
     *
     * @param program программа, не должна быть null
     * @throws UnsuccessException в одном из тестов был получен неверный ответ (вывод)
     * @throws TestingInternalServerErrorException ошибка на сервере (не найдены тесты и т.д.)
     * @throws TestingTimeLimitExceededException программа превысила лимит времени
     * @throws RunTimeErrorException ошибка времени выполнения
     */
    public void execute(Program program) throws
            UnsuccessException,
            TestingInternalServerErrorException,
            RunTimeErrorException,
            TestingTimeLimitExceededException {
        if (program.problem != null && program.problem.n > 0) {
            for (int i = 0; i < program.problem.n; ++i) { // тестируем программу тест за тестом до первой ошибки
                testProgram(program, i);
            }
        } else {
            throw new TestingInternalServerErrorException("System tests not found");
        }
    }

    /**
     * Метод обрабатывает сообщение, которое было получено со стандартного вывода
     * ошибок: заменяет вхождения имени файла с исходным кодом программы на слово
     * "&lt;code&gt;". Поле message не должно быть null, то есть вызов возможен
     * только в testProgram() после чтения вывода ошибок.
     *
     * @param p экземпляр программы, при выполнении которой было получено сообщение
     */
    private void processMessage(Program p) {
        int index;
        index = message.indexOf(p.getSrcFileName());
        while (index > -1) {
            message = message.replace(index, index + p.getSrcFileName().length(), "&lt;code&gt;");
            index = message.indexOf(p.getSrcFileName());
        }
    }

    /**
     * Тестирует программу на тесте с номером testNumber. Метод обладает
     * сложной логикой из-за дурацких исключений.
     *
     * @param program программа
     * @param testNumber номер теста
     * @throws UnsuccessException программа выдает неверный результат
     * @throws TestingInternalServerErrorException ошибка на сервере
     * @throws RunTimeErrorException ошибка времени выполнения
     * @throws TestingTimeLimitExceededException превышен лимит времени
     */
    private void testProgram(Program program, int testNumber) throws
            UnsuccessException,
            TestingInternalServerErrorException,
            RunTimeErrorException,
            TestingTimeLimitExceededException {
        BufferedWriter inputWriter = null; // пишет на вход программе
        BufferedReader testInputReader = null; // читает входной тест
        BufferedReader outputReader = null; // читает выход программы
        BufferedReader testOutputReader = null; // читает выходной тест
        BufferedReader errorReader = null; // читает стандартный вывод ошибок программы
        ProcessExecutor executor = new ProcessExecutor(
                program.getExecuteCmd(),
                program.getDirPath(), 3000); // выполняет программу
        boolean ise = false; // internal server error
        try {
            executor.execute(); // throws ProcessRunningException (невозможно в данном случае),
            // ProcessCanNotBeRunException (ошибка на сервере - нельзя запустить процесс)
            inputWriter = new BufferedWriter(
                    new OutputStreamWriter(executor.getOutputStream())); // throws ProcessNotRunningException
            testInputReader = new BufferedReader(
                    inputGenerator.getReader(testNumber));
            inputDataProcessor.process(inputWriter, testInputReader); // посылает данные на вход

            outputReader = new BufferedReader(
                    new InputStreamReader(executor.getInputStream())); // throws ProcessNotRunningException
            testOutputReader = new BufferedReader(
                    outputGenerator.getReader(testNumber));
            outputDataProcessor.process(outputReader, testOutputReader); // читает выходные данные, сравнивает с эталонными
        } catch (InputWriteException e) {
            // Если не работает запись на вход программы или чтение выхода
            // программы, то произошла ошибка времени выполнения.
        } catch (OutputReadException e) {
            // В блоке finally будет проверен код выхода программы.
        } catch (ProcessExecutingException ex) { // из executor.execute(), ошибка запуска программы
            throw new TestingInternalServerErrorException("Program running error: " + ex);
        } catch (TestingInternalServerErrorException e) { // при обработке входных/выходных данных, тесты не найдены или не могут быть прочитаны
            ise = true;
            throw e;
        } catch (ComparisonFailedException e) { // при обработке выходных данных, может быть ошибка времени выполнения
            throw new UnsuccessException(e.getMessage());
        } finally {
            if (executor.isRunning()) { // если программа была запущена, надо ее завершить
                // читает стандартный вывод ошибок
                message = new StringBuffer();
                try {
                    errorReader = new BufferedReader(new InputStreamReader(executor.getErrorStream())); // throws ProcessNotRunning - невозможно
                    String line;
                    while ((line = errorReader.readLine()) != null) { // throws IOException
                        message.append(line + "\n");
                    }
                } catch (Exception e) { // ошибка ввода/вывода
                    e.printStackTrace();
                } finally {
                    FileOperator.close(errorReader);
                }
                // ждем завершения программы
                if (executor.quietStop()) {
                    System.err.println("Process was running for " + executor.getWorkTime());
                    System.err.println("Program in test case " + testNumber + " exited with code " + executor.getCode());
                    if (!ise) { // если тесты были найдены и прочитаны
                        // код также будет выполняться, если была ComparisonFailedException - может быть ошибка времени выполнения
                        if (executor.isOutOfTime()) {
                            throw new TestingTimeLimitExceededException("Program is out of time");
                        }
                        if (executor.getCode() != 0) {
                            processMessage(program);
                            throw new RunTimeErrorException(message.toString());
                        }
                    } // иначе ошибка на сервере
                } // иначе throws ProcessNotRunningException - невозможно, так как программа была запущена,
                // InterruptedException - невозможно
            } // иначе - ошибка на сервере, процесс не был запущен
        }
    }
}
