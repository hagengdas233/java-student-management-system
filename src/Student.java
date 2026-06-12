public class Student {
        static String school="山西大学";
        private String id;
        private String name;
        private int age;
        private int score;
        Student(String id ,String name,int age,int score) {
            this.id=id;
            this.name = name;
            this.age = age;
            this.score=score;
        }
        public void setId(String id){
            this.id=id;
        }
        public void setAge(int age){
            if(age>0){
                this.age=age;
            }
        }
        public void setName(String name){
            this.name=name;
        }
        public void setScore(int score){
            this.score=score;
        }
        public int getScore(){
            return score;
        }
        public String getName(){
            return name;
        }
        public int getAge(){
            return age;
        }
        public String getId(){
            return id;
        }
        static void getSchool(){
            System.out.println(school);
        }
        public void sayHello(){
            System.out.println("学号"+id+"学校"+school+"姓名"+name+"，年龄"+age+"成绩"+score);
        }

}
