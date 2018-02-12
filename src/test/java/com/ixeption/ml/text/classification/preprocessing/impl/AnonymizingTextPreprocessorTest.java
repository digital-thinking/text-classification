package com.ixeption.ml.text.classification.preprocessing.impl;

import com.ixeption.ml.text.classification.TextFeature;
import org.junit.jupiter.api.Test;

import static com.ixeption.ml.text.classification.preprocessing.impl.AnonymizingTextPreprocessor.ReplaceRegEx.*;
import static org.assertj.core.api.Assertions.assertThat;

class AnonymizingTextPreprocessorTest {

    AnonymizingTextPreprocessor cut = new AnonymizingTextPreprocessor();

    @Test
    public void testEmail() {
        assertThat(stringWasReplaced("simple@email.com", PATTERN_EMAIL.placeHolder)).isTrue();
        assertThat(stringWasReplaced("number7933@email123.de", PATTERN_EMAIL.placeHolder)).isTrue();
        assertThat(stringWasReplaced("abc1234.45223@somehting.com", PATTERN_EMAIL.placeHolder)).isTrue();
        assertThat(stringWasReplaced("www.web.de", PATTERN_EMAIL.placeHolder)).isFalse();
    }

    private boolean stringWasReplaced(String s, String placeHolder) {
        String replaced = cut.preprocess(new TextFeature(s));
        return replaced.contains(placeHolder);
    }

    @Test
    public void testEmailBroad() {
        assertThat(stringWasReplaced("number7933[at]email123.de", PATTERN_EMAIL_BROAD.placeHolder)).isTrue();
        assertThat(stringWasReplaced("myname at something.com", PATTERN_EMAIL_BROAD.placeHolder)).isTrue();
        assertThat(stringWasReplaced("reply(at)somthing.com", PATTERN_EMAIL_BROAD.placeHolder)).isTrue();
        assertThat(stringWasReplaced("c9ccfcfa-7057-4dfe-b11f-0cdcd66430a9-2br81(at)reply.net", PATTERN_EMAIL_BROAD.placeHolder)).isTrue();
        assertThat(stringWasReplaced("www.abcdeatweb.de", PATTERN_EMAIL_BROAD.placeHolder)).isFalse();
    }

    @Test
    public void testIBAN() {
        assertThat(stringWasReplaced("DE44 5001 0517 5407 3249 31", PATTERN_IBAN.placeHolder)).isTrue();
        assertThat(stringWasReplaced("GR16 0110 1250 0000 0001 2300 695", PATTERN_IBAN.placeHolder)).isTrue();
        assertThat(stringWasReplaced("GB29 NWBK 6016 1331 9268 19", PATTERN_IBAN.placeHolder)).isTrue();
        assertThat(stringWasReplaced("CH93 0076 2011 6238 5295 7", PATTERN_IBAN.placeHolder)).isTrue();

        assertThat(stringWasReplaced("+49 721 96693-0", PATTERN_IBAN.placeHolder)).isFalse();
        assertThat(stringWasReplaced("+1 415-970-2022", PATTERN_IBAN.placeHolder)).isFalse();
        assertThat(stringWasReplaced("0176 5484685", PATTERN_IBAN.placeHolder)).isFalse();

    }

    @Test
    public void testPhone() {
        assertThat(stringWasReplaced("+49 721 96693-0", PATTERN_LONG_NUMBER.placeHolder)).isTrue();
        assertThat(stringWasReplaced("+1 415-970-2022", PATTERN_LONG_NUMBER.placeHolder)).isTrue();
        assertThat(stringWasReplaced("0176 5484685", PATTERN_LONG_NUMBER.placeHolder)).isTrue();

    }

    @Test
    public void testUrl() {
        assertThat(stringWasReplaced("http://www.my-url.com?abc=22345", PATTERN_URL.placeHolder)).isTrue();
        assertThat(stringWasReplaced("www.mydomain.co.uk/path", PATTERN_URL.placeHolder)).isTrue();
    }

    @Test
    public void testPhrase() {
        String preprocess = cut.preprocess(new TextFeature("Please visit www.mypage.com, write me an email support@mypage.com or call me +43 721 466 448 and send the money to QA58 DOHB 0000 1234 5678 90AB CDEF G. "));
        assertThat(preprocess).contains(PATTERN_IBAN.placeHolder);
        assertThat(preprocess).contains(PATTERN_EMAIL.placeHolder);
        assertThat(preprocess).contains(PATTERN_LONG_NUMBER.placeHolder);
        assertThat(preprocess).contains(PATTERN_URL.placeHolder);

        assertThat(preprocess).doesNotContain("www.mypage.com");
        assertThat(preprocess).doesNotContain("support@mypage.com");
        assertThat(preprocess).doesNotContain("QA58 DOHB 0000 1234 5678 90AB CDEF G");
        assertThat(preprocess).doesNotContain("+43 721 466 448");

    }


}

