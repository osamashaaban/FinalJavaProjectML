package com.example.FinalJavaProjectML;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.measure.NominalScale;
import smile.data.vector.IntVector;
import smile.io.Read;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;



@RestController
public class SpringController {
    DataFrame df;
    List<Job> Jobs = new ArrayList<>();


    @GetMapping("/view")
    public String readData() throws IOException, URISyntaxException {

        String path="D:\\ITI - AI & Machine Learning\\18- Java For Machine Learning\\FinalJavaProjectML\\FinalJavaProjectML\\src\\main\\resources\\Wuzzuf_Jobs.csv";
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader ();
        this.df = Read.csv (path, format);

        ListIterator<Tuple> iterator = this.df.stream ().collect (Collectors.toList ()).listIterator ();
        //System.out.println(this.df.summary ());
        //System.out.println(this.df.schema ());
        int i=4380;
        while (iterator.hasNext () && i>0) {
            Tuple t = iterator.next ();
            Job p = new Job ((String)t.get ("Title"),(String)t.get ("Company"),(String)t.get ("Location"),(String)t.get ("Type"),
                    (String)t.get ("Level"),(String)t.get ("YearsExp"),(String)t.get ("Country"),(String)t.get ("Skills"));

            this.Jobs.add (p);
            i--;
        }
        String html = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Sample of Wuzzuf Data ") +
                "<table style=\"width:100%;text-align: center ; border: 1px solid;\"> <br><br>" +
                "<tr style = \"border: 1px solid\"><th style = \"border: 1px solid\">Title</th ><th style = \"border: 1px solid\">Company</th><th style = \"border: 1px solid\">Location</th style = \"border: 1px solid\"><th style = \"border: 1px solid\">Type</th><th style = \"border: 1px solid\">Level</th><th style = \"border: 1px solid\">YearsExp</th><th style = \"border: 1px solid\">Country</th><th style = \"border: 1px solid\">Skills</th></tr>";
        for (Job j:this.Jobs){
            html += "<tr style = \"border: 1px solid\">\n" +"<td style = \"border: 1px solid\">"+j.getTitle()+"</td>\n" +"<td style = \"border: 1px solid\">"+j.getCompany()+"</td>\n" +"<td style = \"border: 1px solid\">"+j.getLocation()+"</td>\n"
                    +"<td style = \"border: 1px solid\">"+ j.getType() +"</td>\n" +"<td style = \"border: 1px solid\">"+j.getLevel()+"</td>\n" +"<td style = \"border: 1px solid\">"+j.getYears_EXP()+"</td>\n"+"<td style = \"border: 1px solid\">"+j.getCountry()+"</td>\n"+"<td style = \"border: 1px solid\">"+j.getSkills()
                    +"</td>\n"+"  </tr>";
        }

        return html;
    }

    @GetMapping("/describe")
    public String getSummary() throws IOException, URISyntaxException {
        String r = readData();
        String []schemaa = this.df.schema().toString().replace("[","").replace("]","").split(",");

        String web = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Summary and Scehma of Wuzzuf Data ") +
                "<table style=\"width:100%;text-align: center\"> <br> <br>" ;
        web += String.format("<h3 style=\"text-align:center;\"> Total Number of Records in Wuzzuf Data = %d</h3>", this.df.stream().count());
        web += "<h2 style=\"text-align:center;\"> Schema of Wuzzuf Data  </h2>";
        for (String s : schemaa){
            web+= String.format("<h2 style=\"text-align:center;\">  %s</h2>", s);
        }

        return web;
    }
    @GetMapping("/cleanData")
    public String cleanWuzzufData() throws IOException, URISyntaxException {
        String r = readData();
        DataFrame old_df = this.df;
        this.df = DataFrame.of(this.df.stream().distinct().collect(Collectors.toList()));
        this.df = DataFrame.of(this.df.stream().filter(row -> !row.getString("YearsExp").equals("null Yrs of Exp")));

        System.out.println ("Number of non Null rows is: "+this.df.nrows ());
        String web = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Data Cleaning") +
                "<table style=\"width:100%;text-align: center\">" ;
        web += String.format("<h2 style=\"text-align:center;\"> Total Number of Records Before Removing Null values = %d</h2>", old_df.stream().count());
        web += String.format("<h2 style=\"text-align:center;\"> Total Number of Records After Removing Null values  = %d</h2>", this.df.size());
        web+=String.format("<h2 style=\"text-align:center;\"> Number of Null Rows = %d</h2>", old_df.stream().count() - this.df.stream().count()) ;



        return web;
    }

    public LinkedHashMap<String, Integer> SortByValue (HashMap<String, Integer> ToSortCompany) {
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(ToSortCompany.entrySet());
        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> es1, Map.Entry<String, Integer> es2) {
                return es2.getValue().compareTo(es1.getValue());
            }
        });
        LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public List<Job> GetAllData() {
        List<Job> AllJobs=new ArrayList<>();

        DataFrame withoutDupes = DataFrame.of(df.stream().distinct().collect(Collectors.toList()));
        DataFrame nonNullData = DataFrame.of(withoutDupes.stream().filter(row -> !row.getString("YearsExp").equals("null Yrs of Exp")));

        ListIterator<Tuple> iterator = nonNullData.stream ().collect (Collectors.toList ()).listIterator ();

        int i = nonNullData.size();
        while (iterator.hasNext () && i>0) {
            Tuple t = iterator.next ();
            Job p = new Job ((String)t.get ("Title"),(String)t.get ("Company"),(String)t.get ("Location"),(String)t.get ("Type"),
                    (String)t.get ("Level"),(String)t.get ("YearsExp"),(String)t.get ("Country"),(String)t.get ("Skills"));
            AllJobs.add (p);
            i--;
        }
        return AllJobs;
    }

    @GetMapping("/countJob")
    public String CountJobsForEachCompany () throws IOException, URISyntaxException {
        String r = readData();
        List<Job> ALLDATA = GetAllData();
        HashMap<String, Integer> CountCompany = new HashMap<>();
        for(Job job : ALLDATA){
            if (CountCompany.containsKey(job.getCompany())){
                CountCompany.put(job.getCompany(), CountCompany.get(job.getCompany()) + 1);
            }
            else{
                CountCompany.put(job.getCompany(), 1);
            }
        }
        HashMap<String, Integer> AfterSorting = SortByValue(CountCompany);

        List<String> companies = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        short n = 0;
        for (String a : AfterSorting.keySet()) {
            companies.add(a);
            counts.add(AfterSorting.get(a));
            n++;

        }
        Showpiechart(companies , counts);
        Iterator<String> companiesIterator = companies.iterator();
        Iterator<Integer> countsIterator = counts.iterator();
        String html = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Count the jobs for each company ") +
                "<h3 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">The most demanding companies for jobs is :  " + companies.get(0) + "</h3>" +
                "<table style=\"width:100%;text-align: center;border: 1px solid\">" +
                "<tr style=\"border: 1px solid\"><th style=\"border: 1px solid\">Company</th><th style=\"border: 1px solid\">Count</th></tr>";
        while (companiesIterator.hasNext() && countsIterator.hasNext()) {
            html += "<tr style=\"border: 1px solid\">\n" +"<td style=\"border: 1px solid\">"+companiesIterator.next()+"</td>\n" +"<td style=\"border: 1px solid\">"+countsIterator.next()+"</td>\n" + "  </tr>";
        }

        return html;
    }
    @GetMapping("/JobTitles")
    public String mostPopularJobTitles() throws IOException, URISyntaxException {
        String r = readData();
        List<Job> ALLDATA = GetAllData();
        HashMap<String, Integer> CountJobTitles = new HashMap<>();
        for(Job job : ALLDATA){
            if (CountJobTitles.containsKey(job.getTitle())){
                CountJobTitles.put(job.getTitle(), CountJobTitles.get(job.getTitle()) + 1);
            }
            else{
                CountJobTitles.put(job.getTitle(), 1);
            }
        }
        HashMap<String, Integer> AfterSorting = SortByValue(CountJobTitles);

        List<String> JobTitles = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        short n = 0;
        for (String a : AfterSorting.keySet()) {
            JobTitles.add(a);
            counts.add(AfterSorting.get(a));
            n++;

        }
        showJobsTitleBarChart(JobTitles , counts);
        Iterator<String> titlesIterator = JobTitles.iterator();
        Iterator<Integer> countsIterator = counts.iterator();
        String web = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Job Titles ") +
                "<h3 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">The most popular job titles: " + JobTitles.get(0) + "</h3>" +
                "<table style=\"width:100%;text-align: center;border: 1px solid\">";
        web +=  "<tr style=\"border: 1px solid\"><th style=\"border: 1px solid\">JobTitles</th><th style=\"border: 1px solid\">Count</th></tr>";
        while (titlesIterator.hasNext() && countsIterator.hasNext()) {
            web += "<tr style=\"border: 1px solid\">\n" +"<td style=\"border: 1px solid\">"+titlesIterator.next()+"</td>\n" +"<td style=\"border: 1px solid\">"+countsIterator.next()+"</td>\n" + "  </tr>";
        }
        return web;
    }

// Displays the most popular Areas.
    @GetMapping("/popularAreas")
    public String mostPopularAreas() throws IOException, URISyntaxException {
        String r = readData();
        List<Job> ALLDATA = GetAllData();
        HashMap<String, Integer> CountAreas= new HashMap<>();
        for(Job job : ALLDATA){
            if (CountAreas.containsKey(job.getLocation())){
                CountAreas.put(job.getLocation(), CountAreas.get(job.getLocation()) + 1);
            }
            else{
                CountAreas.put(job.getLocation(), 1);
            }
        }
        HashMap<String, Integer> AfterSorting = SortByValue(CountAreas);

        List<String> Areas = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        short n = 0;
        for (String a : AfterSorting.keySet()) {
            Areas.add(a);
            counts.add(AfterSorting.get(a));
            n++;

        }
        showAreasBarChart(Areas , counts);
        Iterator<String> AreasIterator = Areas.iterator();
        Iterator<Integer> countsIterator = counts.iterator();

        String web = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Areas ") +
                "<h3 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">The most Poplar Area : " + Areas.get(0) + "</h3>" +
                "<table style=\"width:100%;text-align: center;border: 1px solid\">";
        web +=  "<tr style=\"border: 1px solid\"><th style=\"border: 1px solid\">JobTitles</th><th style=\"border: 1px solid\">Count</th></tr>";
        while (AreasIterator.hasNext() && countsIterator.hasNext()) {
            web += "<tr style=\"border: 1px solid\" >\n" +"<td style=\"border: 1px solid\">"+AreasIterator.next()+"</td>\n" +"<td style=\"border: 1px solid\">"+countsIterator.next()+"</td>\n" + "  </tr>";
        }
        return web;

    }

    @GetMapping("/skills")
    public String mostRepeatedSkills() throws IOException, URISyntaxException {
        String r = readData();
        List<Job> ALLDATA = GetAllData();
        HashMap<String, Integer> CountSkills = new HashMap<>();
        for(Job job : ALLDATA){
            String [] temp = job.getSkills().split(",");
            for(String s : temp){
                if (CountSkills.containsKey(s)){
                    CountSkills.put(s, CountSkills.get(s) + 1);
                }
                else{
                    CountSkills.put(s, 1);
                }
            }
        }
        HashMap<String, Integer> AfterSorting = SortByValue(CountSkills);

        List<String> skills = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        short n = 0;
        for (String a : AfterSorting.keySet()) {
            skills.add(a);
            counts.add(AfterSorting.get(a));
            n++;

        }
        showMostRepeatedSkillsPieChart(skills , counts);
        Iterator<String> skillsIterator = skills.iterator();
        Iterator<Integer> countsIterator = counts.iterator();

        String web = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Skills") +
                "<h3 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">The most repeated Skill :  " + skills.get(0) + "</h3>" +
                "<table style=\"width:100%;text-align: center;border: 1px solid\">";
        web +=  "<tr style=\"border: 1px solid\"><th style=\"border: 1px solid\">Skills</th><th style=\"border: 1px solid\">Count</th></tr>";
        while (skillsIterator.hasNext() && countsIterator.hasNext()) {
            web += "<tr style=\"border: 1px solid\">\n" +"<td style=\"border: 1px solid\">"+skillsIterator.next()+"</td>\n" +"<td style=\"border: 1px solid\">"+countsIterator.next()+"</td>\n" + "  </tr>";
        }
        return web;


    }








    public void Showpiechart (List<String> companies, List<Integer> counts) {

        PieChart chart = new PieChartBuilder().width(1500).height(900).title("Count the jobs for each company (Pie Chart )").build();
        int limit = 10;
        for (int i = 0; i < limit; i++) {
            chart.addSeries(companies.get(i), counts.get(i));
        }
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "./PieChart_compaines.png", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showJobsTitleBarChart(List<String> jobTitles, List<Integer> counts){

        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("JobsTitles").xAxisTitle("Job Titles").yAxisTitle("Count").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);

        chart.addSeries ("Counts Of Job Titles ",jobTitles.stream().limit(5).collect(Collectors.toList()), counts.stream().limit(5).collect(Collectors.toList()));
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "./barChart_titles", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    public void showAreasBarChart(List<String> Areas, List<Integer> counts){

        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Areas").xAxisTitle("Areas").yAxisTitle("Count").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler ().setStacked (true);
        chart.addSeries ("Counts Of Areas ",Areas.stream().limit(5).collect(Collectors.toList()), counts.stream().limit(5).collect(Collectors.toList()));
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "./barchart_areas", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    public void showMostRepeatedSkillsPieChart(List<String> skills, List<Integer> counts) {

        PieChart chart = new PieChartBuilder().width(1500).height(900).title("Most Repeated Skills  (Pie Chart )").build();
        int limit = 10;
        for (int i = 0; i <= limit; i++) {
            chart.addSeries(skills.get(i), counts.get(i));
        }
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "./PieChart_skills", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  @GetMapping("/piechart1")
    public ResponseEntity<byte[]> getpieChart_1() {

        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File("./PieChart_compaines.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
    @GetMapping("/barchart1")
    public ResponseEntity<byte[]> getBarChart_1() {

        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File("D:\\ITI - AI & Machine Learning\\18- Java For Machine Learning\\FinalJavaProjectML\\barChart_titles.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
    @GetMapping("/barchart2")
    public ResponseEntity<byte[]> getBarChart_2() {

        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File("D:\\ITI - AI & Machine Learning\\18- Java For Machine Learning\\FinalJavaProjectML\\barchart_areas.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
    @GetMapping("/piechart2")
    public ResponseEntity<byte[]> getPieChart_2() {

        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File("D:\\ITI - AI & Machine Learning\\18- Java For Machine Learning\\FinalJavaProjectML\\PieChart_skills.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
    public int[] factorization(String columnName)
    {
        String[] values = df.stringVector (columnName).distinct ().toArray (new String[]{});
        return df.stringVector (columnName).factorize (new NominalScale(values)).toIntArray ();

    }
    @GetMapping("/factorize")
    public String factorizeYrsOfExp()
    {

        this.df = this.df.merge(IntVector.of("YearsExpFact", factorization("YearsExp")));
        String html = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Factorize Years of Exp ") +
                "<table style=\"width:100%;text-align: center;border: 1px solid\">" +
                "<tr style=\"border: 1px solid\"><th style=\"border: 1px solid\">String</th><th style=\"border: 1px solid\">Factorized</th></tr>";

        ListIterator<Tuple> iterator = this.df.stream ().collect (Collectors.toList ()).listIterator ();

        while (iterator.hasNext()) {
            Tuple t = iterator.next ();
            html += "<tr>\n" +"<td>"+(String) t.get("YearsExp") +"</td>\n" +"<td>"+ t.get("YearsExpFact")+"</td>\n" +"</tr>";

        }

        return html;
    }

    @GetMapping("/getDataHtml")
    public String get_html_data(){

        List<Job> ALLDATA = GetAllData();
        String html = String.format("<h1 style=\"text-align:center;font-family:verdana;background-color:FF8AAE;\">%s</h1>", "Sample of Wuzzuf Data ") +
                "<table style=\"width:100%;text-align: center ; border: 1px solid;\"> <br><br>" +
                "<tr style = \"border: 1px solid\"><th style = \"border: 1px solid\">Title</th ><th style = \"border: 1px solid\">Company</th><th style = \"border: 1px solid\">Location</th style = \"border: 1px solid\"><th style = \"border: 1px solid\">Type</th><th style = \"border: 1px solid\">Level</th><th style = \"border: 1px solid\">YearsExp</th><th style = \"border: 1px solid\">Country</th><th style = \"border: 1px solid\">Skills</th></tr>";
        for (Job j: ALLDATA){
            html += "<tr style = \"border: 1px solid\">\n" +"<td style = \"border: 1px solid\">"+j.getTitle()+"</td>\n" +"<td style = \"border: 1px solid\">"+j.getCompany()+"</td>\n" +"<td style = \"border: 1px solid\">"+j.getLocation()+"</td>\n"
                    +"<td style = \"border: 1px solid\">"+ j.getType() +"</td>\n" +"<td style = \"border: 1px solid\">"+j.getLevel()+"</td>\n" +"<td style = \"border: 1px solid\">"+j.getYears_EXP()+"</td>\n"+"<td style = \"border: 1px solid\">"+j.getCountry()+"</td>\n"+"<td style = \"border: 1px solid\">"+j.getSkills()
                    +"</td>\n"+"  </tr>";
        }

        return html;
    }


}

