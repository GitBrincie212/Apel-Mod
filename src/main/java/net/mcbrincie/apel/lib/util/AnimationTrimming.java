package net.mcbrincie.apel.lib.util;

@SuppressWarnings("unused")
public class AnimationTrimming<T> {
    protected T start;
    protected T end;

    public AnimationTrimming(T start, T end) {
        this.start = start;
        this.end = end;
    }

    public AnimationTrimming(T start) {
        this.start = start;
    }

    public void setStart(T newEnd) {this.start = newEnd;}
    public void setEnd(T newStart) {this.start = newStart;}

    public T getStart() {return this.start;}
    public T getEnd() {return this.end;}
}
