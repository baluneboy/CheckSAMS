package com.example.ken.checksams;

// TODO need base utility project for utils like this one (no longer used in this app)
public class Tuples {

    public static <T1,T2> Tuple2<T1,T2> tuple2(T1 t1, T2 t2){
        return new Tuples.Tuple2<T1,T2>(t1,t2);
    }
    public static class Tuple2<T1,T2> {
      protected T1 t1;
      protected T2 t2;
      public Tuple2(T1 f1, T2 f2) {
        this.t1 = f1; this.t2 = f2;
      }
      public T1 getT1() {return t1;}
      public T2 getT2() {return t2;}
    }

    public static <T1,T2,T3> Tuple3<T1,T2,T3> tuple3(T1 t1, T2 t2, T3 t3){
        return new Tuples.Tuple3<T1,T2,T3>(t1,t2,t3);
    }
    public static class Tuple3<T1,T2,T3> {
        protected T1 t1;
        protected T2 t2;
        protected T3 t3;
        public Tuple3(T1 f1, T2 f2, T3 f3) {
            this.t1 = f1; this.t2 = f2; this.t3 = f3;
        }
        public T1 getT1() {return t1;}
        public T2 getT2() {return t2;}
        public T3 getT3() {return t3;}
    }

    public static <T1,T2,T3,T4> Tuple4<T1,T2,T3,T4> tuple4(T1 t1, T2 t2, T3 t3, T4 t4){
        return new Tuples.Tuple4<T1,T2,T3,T4>(t1,t2,t3,t4);
    }
    public static class Tuple4<T1,T2,T3,T4> {
        protected T1 t1;
        protected T2 t2;
        protected T3 t3;
        protected T4 t4;
        public Tuple4(T1 f1, T2 f2, T3 f3, T4 f4) {
            this.t1 = f1; this.t2 = f2;
            this.t3 = f3; this.t4 = f4;
        }
        public T1 getT1() {return t1;}
        public T2 getT2() {return t2;}
        public T3 getT3() {return t3;}
        public T4 getT4() {return t4;}
    }

}