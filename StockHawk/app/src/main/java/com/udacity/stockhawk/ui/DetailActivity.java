package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.R.attr.fillColor;
import static com.udacity.stockhawk.R.id.chart;
import static com.udacity.stockhawk.R.id.symbol;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;

    @BindView(chart)
    LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
        ButterKnife.bind(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                getIntent().getData(),
                Contract.Quote.QUOTE_COLUMNS,
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
            String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
            setTitle(symbol);

            String history = data.getString(Contract.Quote.POSITION_HISTORY);
            String[] historicalQuotes = history.split("\\n");
            Collections.reverse(Arrays.asList(historicalQuotes));

            List<Entry> entries = new ArrayList<Entry>();
            final String[] xValue = new String[historicalQuotes.length];

            for(int i=0;i<historicalQuotes.length;i++) {
                String[] histroyData = historicalQuotes[i].split(",");
                entries.add(new Entry((float)i, Float.valueOf(histroyData[1])));
                //Converting date (in milliseconds) in simple date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(histroyData[0]));
                xValue[i] = dateFormat.format(calendar.getTime());
            }

            LineDataSet dataSet = new LineDataSet(entries, getString(R.string.chart_label)); // add entries to dataset

            LineData lineData = new LineData(dataSet);
            Description description = new Description();
            description.setText("");
            mChart.setDescription(description);
            mChart.setData(lineData);
            mChart.invalidate(); // refresh

            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xValue[(int) value];
                }
            };

            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
