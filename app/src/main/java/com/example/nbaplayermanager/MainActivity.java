package com.example.nbaplayermanager;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText etPlayerId, etPlayerName;
    private EditText etPlayerTeam, etPlayerPoints;
    private Button btnInsert, btnSearch, btnSelect, btnUpdate, btnDelete;
    private TextView tvContent;

    private FirestoreDAO fsDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPlayerId = findViewById(R.id.etPlayerId);
        etPlayerName = findViewById(R.id.etPlayerName);
        etPlayerTeam = findViewById(R.id.etPlayerTeam);
        etPlayerPoints = findViewById(R.id.etPlayerPoints);

        btnInsert = findViewById(R.id.btnInsert);
        btnSearch = findViewById(R.id.btnSearch);
        btnSelect = findViewById(R.id.btnSelect);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        tvContent = findViewById(R.id.tvContent);

        fsDAO = new FirestoreDAO();

        // Add player
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = etPlayerId.getText().toString().trim();
                String name = etPlayerName.getText().toString().trim();
                String team = etPlayerTeam.getText().toString().trim();
                String pointsText = etPlayerPoints.getText()
                        .toString().trim().replace(',', '.');

                if (id.isEmpty() || name.isEmpty() ||
                        team.isEmpty() || pointsText.isEmpty()) {

                    Toast.makeText(MainActivity.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                double points;

                try {
                    points = Double.parseDouble(pointsText);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this,
                            "Invalid points value",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                fsDAO.createDocument(name, id, team, points);

                tvContent.setText("Saving player...");
            }
        });

        // Search player by ID
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = etPlayerId.getText().toString().trim();

                if (id.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Enter player ID",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                tvContent.setText("Searching...");

                fsDAO.readDocumentById(id, new IFirestoreCallback() {
                    @Override
                    public void onDataReceived(ArrayList<Player> players) {

                        if (players.isEmpty()) {
                            tvContent.setText("Player not found");
                        } else {
                            tvContent.setText(players.get(0).toString());
                        }
                    }
                });
            }
        });

        // Show all players
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvContent.setText("Loading players...");

                fsDAO.readAllDocuments(new IFirestoreCallback() {
                    @Override
                    public void onDataReceived(ArrayList<Player> players) {

                        if (players.isEmpty()) {
                            tvContent.setText("No players found");
                            return;
                        }

                        StringBuilder result = new StringBuilder();

                        for (Player player : players) {
                            result.append(player.toString());
                            result.append("\n");
                        }

                        tvContent.setText(result.toString());
                    }
                });
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = etPlayerId.getText().toString().trim();
                String pointsText = etPlayerPoints.getText().toString().trim();

                if (id.isEmpty() || pointsText.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Enter player ID and new points",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double newPoints = Double.parseDouble(
                            pointsText.replace(',', '.'));
                    fsDAO.updateDocument(id, newPoints);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this,
                            "Invalid points value",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = etPlayerId.getText().toString().trim();

                if (id.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Enter player ID",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                fsDAO.deleteDocument(id);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        fsDAO.listenToAllDocumentsChange();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fsDAO.stopListening();
    }

}
