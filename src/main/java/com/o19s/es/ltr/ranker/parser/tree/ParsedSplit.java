package com.o19s.es.ltr.ranker.parser.tree;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.List;

/**
 * Created by doug on 5/26/17.
 */
public class ParsedSplit {
    public static final String NAME = "json-ltr-split-parser";
    private static final ObjectParser<ParsedSplit, ParsedSplit.SplitContext> PARSER;
    private static final ObjectParser<SplitOrOutput, ParsedSplit.SplitContext> OUTPUT_OR_SPLIT_PARSER;


    static {
        PARSER = new ObjectParser<>(NAME, ParsedSplit::new);
        PARSER.declareString((split, featureName) -> split.setFeatureName(featureName),
                             new ParseField("feature"));

        PARSER.declareDouble((split, thresholdValue) -> split.setThreshould(thresholdValue),
                new ParseField("threshold"));

        PARSER.declareObject( ParsedSplit::setLhs,
                              (xParser, context) -> context.parseOutputOrSplit(xParser),
                              new ParseField("lhs"));

        PARSER.declareObject( ParsedSplit::setRhs,
                (xParser, context) -> context.parseOutputOrSplit(xParser),
                new ParseField("rhs"));

        // In the child objects, we'll eithre encounter another split, or an output value
        OUTPUT_OR_SPLIT_PARSER = new ObjectParser<>(NAME, SplitOrOutput::new);
        OUTPUT_OR_SPLIT_PARSER.declareDouble((split, outputValue) -> split.setOutput(outputValue),
                                            new ParseField("output"));

        OUTPUT_OR_SPLIT_PARSER.declareObject( (splitOrObj, newSplit) -> splitOrObj.setSplit(newSplit),
                (xParser, context) -> context.parseSplit(xParser),
                new ParseField("split"));


    }


    public static class SplitOrOutput {

        public ParsedSplit split;

        public SplitOrOutput() {
            split = null;
        }

        public void setOutput(double out) {
            split = new ParsedSplit();
            split.setOutput(out);
        }


        public void setSplit(Object out) {
            split = (ParsedSplit)out;
        }

    }


    public static class SplitContext {

        public ParsedSplit parseOutputOrSplit(XContentParser parser) throws IOException {
            SplitOrOutput splOrOut = OUTPUT_OR_SPLIT_PARSER.parse(parser, new SplitContext());
            return splOrOut.split;
        }

        public ParsedSplit parseSplit(XContentParser parser) throws IOException {
            return ParsedSplit.parse(parser);
        }

    }

    public ParsedSplit() {

    }

    public void setFeatureName(String name) {
        featureName = name;
    }

    public void setLhs(ParsedSplit split) {
        lhs = split;
    }

    public void setRhs(ParsedSplit split) {
        rhs = split;
    }


    public void setOutput(double val) {
        output = val;
    }

    public ParsedSplit getLhs() {
        return lhs;
    }

    public ParsedSplit getRhs() {
        return rhs;
    }

    public double getThreshold() {
        return threshold;
    }

    public double getOutput() {
        return output;
    }

    public String getFeature() {
        return featureName;
    }


    public void setThreshould(double val) {
        threshold = val;
    }

    public String getFeatureName() {
        return featureName;
    }

    public static ParsedSplit parse(XContentParser xParser) throws IOException {
        return PARSER.parse(xParser, new SplitContext());
    }

    private String featureName;
    private double threshold;
    private double output;
    private ParsedSplit lhs;
    private ParsedSplit rhs;


}
