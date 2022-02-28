package com.luzianu;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static int value;

    public static void main(String[] args) throws IOException {
        if (Runtime.getRuntime().availableProcessors() <= 1)
            throw new RuntimeException("There is only one available processor?!");

        // all cores except 0 (Process.ProcessorAffinity powershell variable)
        value = (int) (Math.pow(2, Runtime.getRuntime().availableProcessors())) - 2;

        // UI stuff
        JFrame frame = new JFrame(Runtime.getRuntime().availableProcessors() + " cores detected");
        frame.setResizable(false);
        frame.setMaximumSize(frame.getMinimumSize());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(350, 30));
        frame.add(progressBar, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // figure out username
        String user = execCmd("whoami").trim();
        String cmd = "tasklist /FI \"USERNAME eq " + user + "\"";
        String result = execCmd(cmd).trim();

        // processes to ignore
        List<String> ignoreProcesses = new ArrayList<>();
        ignoreProcesses.add("osu!.exe");
        ignoreProcesses.add("java.exe");
        ignoreProcesses.add("javaw.exe");
        ignoreProcesses.add("explorer.exe");
        ignoreProcesses.add("idea64.exe");

        String[] tasks = result.split(System.lineSeparator());
        Set<String> set = new HashSet<>();

        // parse cmd task list
        for (int i = 2; i < tasks.length; i++) {
            String[] arr = tasks[i].split("(  )+");
            if (arr.length == 4) {
                String name = arr[0].trim();

                if (!ignoreProcesses.contains(name)) {
                    if (name.contains(".")) {
                        String pn = name.substring(0, name.lastIndexOf("."));
                        set.add(pn);
                    }
                }
            }
        }

        progressBar.setMaximum(set.size());

        // set all user processes to not use CPU0 (first core)
        int counter = 0;
        for (String task : set) {
            updateProgressBar(progressBar, counter, task);

            String c = "Powershell \"ForEach($PROCESS in GET-PROCESS " + task + ") { $PROCESS.ProcessorAffinity=" + value + "}\"";
            System.out.println(c);
            System.out.println(getOutputFromProgram(c));
            updateProgressBar(progressBar, ++counter, task);
        }

        System.exit(0);
    }

    public static void updateProgressBar(JProgressBar progressBar, int value, String name) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            progressBar.setString(Main.value + ": " + name + " (" + value + "/" + progressBar.getMaximum() + ")");
        });
    }

    public static String execCmd(String cmd) throws java.io.IOException {
        Process process = Runtime.getRuntime().exec(cmd);

        String str = "";
        {
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = null;

            while ((line = reader.readLine()) != null) {
                str += line + System.lineSeparator();
            }
        }

        return str.trim();
    }

    public static String getOutputFromProgram(String program) throws IOException {
        Process proc = Runtime.getRuntime().exec(program);
        return Stream.of(proc.getErrorStream(), proc.getInputStream()).parallel().map((InputStream isForOutput) -> {
            StringBuilder output = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(isForOutput))) {
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line);
                    output.append("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return output;
        }).collect(Collectors.joining());
    }
}
